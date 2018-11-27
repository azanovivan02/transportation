package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.netcracker.algorithms.auction.concurrent.ConcurrentAssignmentPhaseUtils.*;
import static com.netcracker.algorithms.auction.entities.BidUtils.getTotalVolume;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

public class EndBarrierAction implements Runnable {

    private final static Comparator<Bid> valueAscendingBidComparator = comparingDouble(Bid::getBidValue);

    final ConcurrentFlowMatrix flowMatrix;
    final Set<Bid> bidSet;
    final int sinkAmount;

    public EndBarrierAction(ConcurrentFlowMatrix flowMatrix, Set<Bid> bidSet, int sinkAmount) {
        this.flowMatrix = flowMatrix;
        this.bidSet = bidSet;
        this.sinkAmount = sinkAmount;
    }

    @Override
    public void run() {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            acceptBidsForSink(sinkIndex);
        }
    }

    private void acceptBidsForSink(int sinkIndex) {
        info("\n=== Processing bids for sink %d ==============\n", sinkIndex);

        final List<Bid> bidList = bidSet
                .stream()
                .filter(bid -> bid.getSinkIndex() == sinkIndex)
                .sorted(valueAscendingBidComparator)
                .collect(toList());

        // Reading and writing in shared mutable state, synchonozation happens only because of barriers
        final int maxVolumeForSink = flowMatrix.getMaxVolumeForSink(sinkIndex);
        final List<Bid> acceptedBidList = chooseBidsToAccept(sinkIndex, bidList, maxVolumeForSink);
        final Integer acceptedBidVolume = getTotalVolume(acceptedBidList);
        removeLeastExpensiveFlows(sinkIndex, flowMatrix, acceptedBidVolume);
        addFlowsForAcceptedBids(sinkIndex, flowMatrix, acceptedBidList);

        assertThatNewVolumeIsCorrect(sinkIndex, flowMatrix);
    }
}
