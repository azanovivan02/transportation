package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.entities.*;
import com.netcracker.algorithms.auction.epsilonScaling.DefaultEpsilonSequenceProducer;
import com.netcracker.algorithms.auction.epsilonScaling.EpsilonSequenceProducer;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentUtils.*;
import static com.netcracker.algorithms.auction.entities.FlowUtils.getTotalVolume;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;

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

        final BidMap bidMap = new BidMap();
        final AtomicInteger currentSourceIndex = new AtomicInteger();

        final CyclicBarrier startBarrier = new CyclicBarrier(runnableAmount, ()->{
            synchronized (bidMap) {
                bidMap.clear();
            }
            currentSourceIndex.set(-1);
        });

        final CyclicBarrier endBarrier = new CyclicBarrier(runnableAmount, ()->{
            Comparator<Bid> bidComparator = comparingDouble(Bid::getBidValue);
            for (int sinkIndex = 0; sinkIndex < sinkArray.length; sinkIndex++) {

                List<Bid> bidList;
                synchronized (bidMap) {
                    bidList = bidMap.getBidsForSink(sinkIndex);
                }

                bidList.sort(bidComparator);
                info("\n=== Processing bids for sink %d ==============\n", sinkIndex);

                List<Bid> acceptedBidList = ConcurrentAssignmentPhaseUtils.chooseBidsToAccept(sinkIndex, flowMatrix, bidList);
                Integer acceptedBidVolume = BidUtils.getTotalVolume(acceptedBidList);
                ConcurrentAssignmentPhaseUtils.removeLeastExpensiveFlows(sinkIndex, flowMatrix, acceptedBidVolume);
                ConcurrentAssignmentPhaseUtils.addFlowsForAcceptedBids(sinkIndex, flowMatrix, acceptedBidList);

                ConcurrentAssignmentPhaseUtils.assertThatNewVolumeIsCorrect(sinkIndex, flowMatrix);
            }
        });

        Runnable biddingRunnable = () -> {
            int iterationNumber = 0;
            while (!flowMatrix.isComplete()) {
                info("\n=== Iteration: %d ======================\n", iterationNumber);
                iterationNumber++;

                awaitBarrier(startBarrier);

                // === Biding =========================================================================================================================================

                while (true) {
                    int sourceIndex = currentSourceIndex.incrementAndGet();
                    if(sourceIndex >= sourceArray.length) {
                        break;
                    }

                    List<Flow> availableFlowList = flowMatrix.getAvailableFlowListForSink(sourceIndex);
                    List<Flow> currentFlowList = flowMatrix.getCurrentFlowListForSource(sourceIndex);
                    int availableVolume = ConcurrentBiddingPhaseUtils.getAvailableVolume(sourceArray[sourceIndex], currentFlowList);

                    List<Flow> desiredFlowList = ConcurrentBiddingPhaseUtils.getAddedFlowList(sourceIndex, availableFlowList, availableVolume, benefitMatrix);

                    customAssert(
                            doubleEquals(getTotalVolume(desiredFlowList), availableVolume)
                    );

                    Flow secondBestFlow = removeLast(availableFlowList);
                    double secondBestFlowPrice = secondBestFlow.getPrice();
                    int secondBestFlowSinkIndex = secondBestFlow.getSinkIndex();
                    double secondBestFlowValue = benefitMatrix[sourceIndex][secondBestFlowSinkIndex] - secondBestFlowPrice;

                    for (Flow desiredFlow : desiredFlowList) {
                        Bid bid = ConcurrentBiddingPhaseUtils.getBidForFlow(desiredFlow, sourceIndex, benefitMatrix[sourceIndex], secondBestFlowValue, epsilon, secondBestFlow);
                        synchronized (bidMap) {
                            bidMap.add(bid);
                        }
                    }
                }

                awaitBarrier(endBarrier);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(EXECUTOR_THREAD_AMOUNT);

//        Future<?> future = executor.submit(biddingRunnable);
//        getFutureResult(future);

        List<Future<?>> futureList = submitRunnableSeveralTimes(biddingRunnable, runnableAmount, executor);
        getFutureList(futureList);

        executor.shutdown();

        info("=== Auction Iteration is finished =================");
        info("Result matrix\n");
        info(flowMatrix.volumeMatrixToString());

        flowMatrix.assertIsValid();

        int[][] volumeMatrix = flowMatrix.getVolumeMatrix();
        return new Allocation(problem, volumeMatrix);
    }
}
