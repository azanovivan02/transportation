package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentBiddingPhaseUtils.createBidForFlow;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentBiddingPhaseUtils.getAddedFlowList;
import static com.netcracker.algorithms.auction.concurrent.ConcurrentBiddingPhaseUtils.getAvailableVolume;
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
    final FlowMatrix flowMatrix;

    final Set<Bid> bidSet;
    final AtomicInteger currentSourceIndex;
    final AtomicBoolean flowMatrixIsComplete;

    final CyclicBarrier startBarrier;
    final CyclicBarrier endBarrier;

    public BiddingRunnable(long id,
                           Double epsilon,
                           int[] sourceArray,
                           int[][] benefitMatrix,
                           FlowMatrix flowMatrix,
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

            if(flowMatrixIsComplete.get()){
                break;
            }

            while (true) {
                int sourceIndex = currentSourceIndex.incrementAndGet();
                if (sourceIndex >= sourceArray.length) {
                    break;
                }
                info("Thread %d creating bids for source %d", id, sourceIndex);

                List<Flow> availableFlowList = flowMatrix.getAvailableFlowListForSink(sourceIndex);
                List<Flow> currentFlowList = flowMatrix.getCurrentFlowListForSource(sourceIndex);
                int availableVolume = getAvailableVolume(sourceArray[sourceIndex], currentFlowList);

                List<Flow> desiredFlowList = getAddedFlowList(sourceIndex, availableFlowList, availableVolume, benefitMatrix);

                customAssert(
                        doubleEquals(getTotalVolume(desiredFlowList), availableVolume)
                );

                Flow secondBestFlow = removeLast(availableFlowList);
                double secondBestFlowPrice = secondBestFlow.getPrice();
                int secondBestFlowSinkIndex = secondBestFlow.getSinkIndex();
                double secondBestFlowValue = benefitMatrix[sourceIndex][secondBestFlowSinkIndex] - secondBestFlowPrice;

                for (Flow desiredFlow : desiredFlowList) {
                    Bid bid = createBidForFlow(
                            desiredFlow,
                            sourceIndex,
                            benefitMatrix[sourceIndex],
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
