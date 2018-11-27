package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.Flow;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentBiddingPhaseUtils.*;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentUtils.awaitBarrier;
import static com.netcracker.algorithms.auction.entities.FlowUtils.getTotalVolume;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

public class BiddingRunnable implements Runnable {

    final long id;

    final Double epsilon;

    final int[] sourceArray;
    final int[][] benefitMatrix;
    final ConcurrentFlowMatrix flowMatrix;

    final Set<Bid> bidSet;
    final AtomicInteger currentSourceIndex;
    final AtomicBoolean flowMatrixIsComplete;

    final CyclicBarrier startBarrier;
    final CyclicBarrier endBarrier;

    public BiddingRunnable(long id,
                           Double epsilon,
                           int[] sourceArray,
                           int[][] benefitMatrix,
                           ConcurrentFlowMatrix flowMatrix,
                           Set<Bid> bidSet,
                           AtomicInteger currentSourceIndex,
                           AtomicBoolean flowMatrixIsComplete,
                           CyclicBarrier startBarrier,
                           CyclicBarrier endBarrier) {
        this.id = id;
        this.epsilon = epsilon;
        this.sourceArray = sourceArray;
        this.benefitMatrix = benefitMatrix;
        this.flowMatrix = flowMatrix;
        this.bidSet = bidSet;
        this.currentSourceIndex = currentSourceIndex;
        this.flowMatrixIsComplete = flowMatrixIsComplete;
        this.startBarrier = startBarrier;
        this.endBarrier = endBarrier;
    }

    @Override
    public void run() {
        while (true) {

            awaitBarrier(startBarrier);

            if (flowMatrixIsComplete.get()) {
                break;
            }

            while (true) {
                final int sourceIndex = currentSourceIndex.incrementAndGet();
                if (sourceIndex >= sourceArray.length) {
                    break;
                }

                info("Thread %d creating bids for source %d", id, sourceIndex);

                // reading from shared mutable state, synchronization happens only by barrier
                final List<Flow> availableFlowList = flowMatrix.getAvailableFlowListForSource(sourceIndex);
                final List<Flow> currentFlowList = flowMatrix.getCurrentFlowListForSource(sourceIndex);

                final int sourceVolume = sourceArray[sourceIndex];
                final int remainingVolume = getRemainingVolume(sourceVolume, currentFlowList);

                final List<Flow> desiredFlowList = getDesiredFlowList(sourceIndex, availableFlowList, remainingVolume, benefitMatrix);

                customAssert(
                        doubleEquals(getTotalVolume(desiredFlowList), remainingVolume)
                );

                final Flow secondBestFlow = removeLast(availableFlowList);
                final double secondBestFlowPrice = secondBestFlow.getPrice();
                final int secondBestFlowSinkIndex = secondBestFlow.getSinkIndex();
                final double secondBestFlowValue = benefitMatrix[sourceIndex][secondBestFlowSinkIndex] - secondBestFlowPrice;

                for (Flow desiredFlow : desiredFlowList) {
                    int desiredFlowSinkIndex = desiredFlow.getSinkIndex();
                    int desiredFlowBenefit = benefitMatrix[sourceIndex][desiredFlowSinkIndex];
                    final Bid bid = createBidForFlow(
                            desiredFlow,
                            desiredFlowBenefit,
                            sourceIndex,
                            secondBestFlowValue,
                            epsilon,
                            secondBestFlow
                    );
                    bidSet.add(bid);
                }
            }

            awaitBarrier(endBarrier);
        }
    }
}
