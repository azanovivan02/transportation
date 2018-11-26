package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.*;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentAssignmentPhaseUtils.*;
import static com.netcracker.algorithms.auction.entities.BidUtils.getTotalVolume;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

public class EndBarrierAction implements Runnable {

    final FlowMatrix flowMatrix;
    final Set<Bid> bidSet;
    final int sinkAmount;

    public EndBarrierAction(FlowMatrix flowMatrix, Set<Bid> bidSet, int sinkAmount) {
        this.flowMatrix = flowMatrix;
        this.bidSet = bidSet;
        this.sinkAmount = sinkAmount;
    }

    @Override
    public void run() {
        synchronized (flowMatrix) {
            Comparator<Bid> bidComparator = comparingDouble(Bid::getBidValue);
            for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {

                final int currentSinkIndex = sinkIndex;

                List<Bid> bidList = bidSet
                        .stream()
                        .filter(bid -> bid.getSinkIndex() == currentSinkIndex)
                        .collect(toList());

                bidList.sort(bidComparator);
                info("\n=== Processing bids for sink %d ==============\n", sinkIndex);

                List<Bid> acceptedBidList = chooseBidsToAccept(sinkIndex, flowMatrix, bidList);
                Integer acceptedBidVolume = getTotalVolume(acceptedBidList);
                removeLeastExpensiveFlows(sinkIndex, flowMatrix, acceptedBidVolume);
                addFlowsForAcceptedBids(sinkIndex, flowMatrix, acceptedBidList);

                assertThatNewVolumeIsCorrect(sinkIndex, flowMatrix);
            }
        }
    }
}
