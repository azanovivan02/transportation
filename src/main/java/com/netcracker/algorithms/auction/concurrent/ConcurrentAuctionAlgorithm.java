package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.FlowMatrix;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentAssignmentPhaseUtils.performConcurrentAssignmentPhase;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentBiddingPhaseUtils.performConcurrentBiddingPhase;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentUtils.getFutureResult;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

public class ConcurrentAuctionAlgorithm implements TransportationProblemSolver {

    public static final int THREAD_AMOUNT = 2;
    private final EpsilonSequenceProducer epsilonProducer;

    public ConcurrentAuctionAlgorithm() {
        this(new DefaultEpsilonSequenceProducer(1.0, 0.25));
    }

    public ConcurrentAuctionAlgorithm(EpsilonSequenceProducer epsilonProducer) {
        this.epsilonProducer = epsilonProducer;
    }

    @Override
    public Allocation findAllocation(TransportationProblem problem) {
        int problemSize = 10;
        final List<Double> epsilonSequence = epsilonProducer.getEpsilonSequence(problemSize);
        final Double epsilon = epsilonSequence.get(0);

        final int[] sourceArray = problem.getSourceArray();
        final int[] sinkArray = problem.getSinkArray();
        final int[][] costMatrix = problem.getCostMatrix();
        final int[][] benefitMatrix = TransportationProblem.convertToBenefitMatrix(costMatrix);

        final FlowMatrix flowMatrix = new FlowMatrix(sourceArray, sinkArray);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_AMOUNT);

        Runnable singleRunnable = () -> {
            int iterationNumber = 0;
            while (!flowMatrix.isComplete()) {
                info("\n=== Iteration: %d ======================\n", iterationNumber);
                iterationNumber++;

                BidMap bidMap = performConcurrentBiddingPhase(
                        flowMatrix,
                        benefitMatrix,
                        sourceArray,
                        epsilon
                );

                customAssert(bidMap.size() != 0, "No new bids");

                performConcurrentAssignmentPhase(
                        flowMatrix,
                        bidMap,
                        sinkArray.length
                );
            }
        };

        Future<?> future = executor.submit(singleRunnable);
        getFutureResult(future);

        executor.shutdown();

        info("=== Auction Iteration is finished =================");
        info("Result matrix\n");
        info(flowMatrix.volumeMatrixToString());

        flowMatrix.assertIsValid();

        int[][] volumeMatrix = flowMatrix.getVolumeMatrix();
        return new Allocation(problem, volumeMatrix);
    }
}
