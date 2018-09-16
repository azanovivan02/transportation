package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.Flow.getVolume;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static java.lang.Math.abs;
import static java.util.Comparator.comparingDouble;

public class AuctionAlgorithm implements TransportationProblemSolver {

    private final EpsilonSequenceProducer epsilonProducer;

    public AuctionAlgorithm() {
        this(new DefaultEpsilonSequenceProducer(1.0, 0.25));
    }

    public AuctionAlgorithm(EpsilonSequenceProducer epsilonProducer) {
        this.epsilonProducer = epsilonProducer;
    }

    @Override
    public Allocation findAllocation(TransportationProblem problem) {
        int problemSize = 10;
        final List<Double> epsilonSequence = epsilonProducer.getEpsilonSequence(problemSize);
        Double epsilon = epsilonSequence.get(0);

        int[] supplyArray = problem.getSupplyArray();
        int[] demandArray = problem.getDemandArray();
        int[][] costMatrix = problem.getCostMatrix();

        FlowMatrix flowMatrix = new FlowMatrix(supplyArray, demandArray);

        for (int sourceIndex = 0; sourceIndex < supplyArray.length; sourceIndex++) {
            List<Flow> availableFlowList = flowMatrix.getAvailableFlowList(sourceIndex);
            int totalVolume = supplyArray[sourceIndex];
            List<Flow> currentFlowList = flowMatrix.getCurrentFlowList(sourceIndex);
            double availableVolume = getAvailableVolume(totalVolume, currentFlowList);

            sortByProfitAscending(availableFlowList, sourceIndex, costMatrix);
            List<Flow> addedFlows = new ArrayList<>();
            double addedVolume = 0.0;
            while (addedVolume < availableVolume) {
                Flow flow = removeLast(availableFlowList);
                customAssert(flow != null, "Source can't consume more than all available flows");
                double remainingVolume = availableVolume - addedVolume;
                double volume = flow.getVolume();
                if (volume <= remainingVolume) {
                    addedVolume += volume;
                    addedFlows.add(flow);
                } else {
                    Flow splittedFlow = flow.split(remainingVolume);
                    addedVolume += remainingVolume;
                    addedFlows.add(splittedFlow);
                }
            }
            customAssert(doubleEquals(addedVolume, availableVolume));

            List<Flow> desiredFlowList = new ArrayList<>();
            desiredFlowList.addAll(addedFlows);
            desiredFlowList.addAll(currentFlowList);
            Double desiredVolume = getVolume(desiredFlowList);

            customAssert(doubleEquals(desiredVolume, totalVolume));

            System.out.println(availableFlowList);
        }

        return new Allocation(problem, new int[3][4]);
    }

    private static void sortByProfitAscending(List<Flow> flowList,
                                              int sourceIndex,
                                              int[][] costMatrix) {
        Comparator<Flow> profitComparator = comparingDouble(flow -> {
            int sinkIndex = flow.getSinkIndex();
            int value = costMatrix[sourceIndex][sinkIndex];
            return flow.getProfit(value);
        });
        flowList.sort(profitComparator);
    }

    public static double getAvailableVolume(double totalVolume,
                                            List<Flow> currentFlowList) {
        double currentVolume = getVolume(currentFlowList);
        return totalVolume - currentVolume;
    }
}
