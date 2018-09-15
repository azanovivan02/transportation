package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

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

        for (int i = 0; i < supplyArray.length; i++) {
            List<Flow> availableFlowList = flowMatrix.getAvailableFlowList(i);
            final int sourceIndex = i;
            Comparator<Flow> comparator = comparingDouble(flow -> {
                int sinkIndex = flow.getSinkIndex();
                int value = costMatrix[sourceIndex][sinkIndex];
                return flow.getProfit(value);
            });
            availableFlowList.sort(comparator.reversed());

            System.out.println(availableFlowList);
        }

        return new Allocation(problem, new int[3][4]);
    }

}
