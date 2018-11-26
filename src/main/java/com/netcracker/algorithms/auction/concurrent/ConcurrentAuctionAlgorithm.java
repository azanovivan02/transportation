package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.*;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netcracker.algorithms.TransportationProblem.convertToBenefitMatrix;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentUtils.executeRunnableList;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Collections.newSetFromMap;

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
        // === immutable data ===============
        final Double epsilon = getEpsilon();
        final int[] sourceArray = problem.getSourceArray();
        final int[] sinkArray = problem.getSinkArray();
        final int[][] benefitMatrix = convertToBenefitMatrix(problem.getCostMatrix());

        // === mutable date ===============
        final ConcurrentFlowMatrix flowMatrix = new ConcurrentFlowMatrix(sourceArray, sinkArray);
        final Set<Bid> bidSet = newSetFromMap(new ConcurrentHashMap<>());
        final AtomicInteger currentSourceIndex = new AtomicInteger();
        final AtomicBoolean assignmentIsComplete = new AtomicBoolean();

        int runnableAmount = 4;

        // === tasks ===============
        StartBarrierAction startBarrierAction = new StartBarrierAction(bidSet, currentSourceIndex, flowMatrix, assignmentIsComplete);
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
                    assignmentIsComplete, startBarrier,
                    endBarrier
            );
            biddingRunnableList.add(biddingRunnable);
        }

        // === execution ===============
        executeRunnableList(biddingRunnableList, EXECUTOR_THREAD_AMOUNT);

        info("=== Auction Iteration is finished =================");
        info("Result matrix\n");
        info(flowMatrix.volumeMatrixToString());
        flowMatrix.assertIsValid();

        int[][] volumeMatrix = flowMatrix.getVolumeMatrix();
        return new Allocation(problem, volumeMatrix);
    }

    private Double getEpsilon() {
        int problemSize = 10;
        final List<Double> epsilonSequence = epsilonProducer.getEpsilonSequence(problemSize);
        return epsilonSequence.get(0);
    }
}
