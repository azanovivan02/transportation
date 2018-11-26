package com.netcracker.algorithms.auction.single;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.FlowMatrix;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.List;

import static com.netcracker.algorithms.auction.single.AssignmentPhaseUtils.performSingleThreadedAssignmentPhase;
import static com.netcracker.algorithms.auction.single.BiddingPhaseUtils.performBiddingPhase;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

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
        int[][] benefitMatrix = TransportationProblem.convertToBenefitMatrix(costMatrix);

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

        info("=== Auction Iteration is finished =================");
        info("Result matrix\n");
        info(flowMatrix.volumeMatrixToString());

        flowMatrix.assertIsValid();

        int[][] volumeMatrix = flowMatrix.getVolumeMatrix();
        return new Allocation(problem, volumeMatrix);
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

        performSingleThreadedAssignmentPhase(
                flowMatrix,
                bidMap,
                sinkArray.length
        );
    }

}
