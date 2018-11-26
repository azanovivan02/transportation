package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.*;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentUtils.executeRunnableList;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

public class ConcurrentAuctionAlgorithm implements TransportationProblemSolver {

    public static final int EXECUTOR_THREAD_AMOUNT = 8;

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

        int runnableAmount = 4;

        final Set<Bid> bidSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        final AtomicInteger currentSourceIndex = new AtomicInteger();

        StartBarrierAction startBarrierAction = new StartBarrierAction(bidSet, currentSourceIndex);
        final CyclicBarrier startBarrier = new CyclicBarrier(runnableAmount, startBarrierAction);

        EndBarrierAction endBarrierAction = new EndBarrierAction(flowMatrix, bidSet, sinkArray.length);
        final CyclicBarrier endBarrier = new CyclicBarrier(runnableAmount, endBarrierAction);

        List<Runnable> biddingRunnableList = new ArrayList<>();
        for (int runnable_id = 0; runnable_id < runnableAmount; runnable_id++) {
            Runnable biddingRunnable = new BiddingRunnable(
                    runnable_id,
                    epsilon,
                    sourceArray,
                    benefitMatrix,
                    flowMatrix,
                    bidSet,
                    currentSourceIndex,
                    startBarrier,
                    endBarrier
            );
            biddingRunnableList.add(biddingRunnable);
        }

        executeRunnableList(biddingRunnableList, EXECUTOR_THREAD_AMOUNT);

        info("=== Auction Iteration is finished =================");
        info("Result matrix\n");
        info(flowMatrix.volumeMatrixToString());
        flowMatrix.assertIsValid();

        int[][] volumeMatrix = flowMatrix.getVolumeMatrix();
        return new Allocation(problem, volumeMatrix);
    }
}
