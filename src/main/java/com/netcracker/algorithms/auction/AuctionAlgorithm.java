package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.FlowMatrix;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.List;

import static com.netcracker.algorithms.auction.AssignmentPhaseUtils.performAssignmentPhase;
import static com.netcracker.algorithms.auction.BiddingPhaseUtils.performBiddingPhase;
import static com.netcracker.utils.GeneralUtils.intMatrixToString;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.lang.Math.abs;

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

        int[] sourceArray = problem.getSourceArray();
        int[] sinkArray = problem.getSinkArray();
        int[][] costMatrix = problem.getCostMatrix();
        int[][] benefitMatrix = convertToBenefitMatrix(costMatrix);

        FlowMatrix flowMatrix = new FlowMatrix(sourceArray, sinkArray);

        int iterationNumber = 0;
        while (!flowMatrix.isComplete()) {
            info("\n=== Iteration: %d ======================\n", iterationNumber);
            iterationNumber++;
            performAuctionIteration(
                    flowMatrix,
                    benefitMatrix,
                    sourceArray,
                    sinkArray,
                    epsilon
            );
        }

        info(flowMatrix.volumeMatrixToString());

        return new Allocation(problem, new int[3][4]);
    }

    private void performAuctionIteration(FlowMatrix flowMatrix,
                                         int[][] benefitMatrix,
                                         int[] sourceArray,
                                         int[] sinkArray,
                                         Double epsilon) {
        BidMap bidMap = performBiddingPhase(
                flowMatrix,
                benefitMatrix,
                sourceArray,
                epsilon
        );

        if (bidMap.size() == 0) {
            throw new IllegalStateException("No new bids");
        }

        performAssignmentPhase(
                flowMatrix,
                sinkArray,
                bidMap
        );
    }

    private static int[][] convertToBenefitMatrix(int[][] costMatrix) {
        int sourceAmout = costMatrix.length;
        int sinkAmount = costMatrix[0].length;
        int maxValue = getMaxValue(costMatrix) + 1;
        int[][] benefitMatrix = new int[sourceAmout][sinkAmount];
        for (int i = 0; i < sourceAmout; i++) {
            for (int j = 0; j < sinkAmount; j++) {
                benefitMatrix[i][j] = maxValue - costMatrix[i][j];
            }
        }

        info("Converted to benefit matrix: ");
        info(intMatrixToString(benefitMatrix));

        return benefitMatrix;
    }

    private static int getMaxValue(int[][] costMatrix) {
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                int currentValue = costMatrix[i][j];
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                }
            }
        }
        return maxValue;
    }
}
