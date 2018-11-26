package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidUtils;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.*;

import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;

public class EndBarrierAction implements Runnable {

    final FlowMatrix flowMatrix;
    final Set<Bid> bidSet;
    final int[] sinkArray;

    public EndBarrierAction(FlowMatrix flowMatrix, Set<Bid> bidSet, int[] sinkArray) {
        this.flowMatrix = flowMatrix;
        this.bidSet = bidSet;
        this.sinkArray = sinkArray;
    }

    @Override
    public void run() {
        Comparator<Bid> bidComparator = comparingDouble(Bid::getBidValue);
        Map<Integer, List<Bid>> bidMap = new HashMap<>();
        for (int sinkIndex = 0; sinkIndex < sinkArray.length; sinkIndex++) {

            final int currentSinkIndex = sinkIndex;

            List<Bid> bidList = bidSet
                    .stream()
                    .filter(bid -> bid.getSinkIndex() == currentSinkIndex)
                    .collect(toList());

            bidList.sort(bidComparator);
            info("\n=== Processing bids for sink %d ==============\n", sinkIndex);

            List<Bid> acceptedBidList = ConcurrentAssignmentPhaseUtils.chooseBidsToAccept(sinkIndex, flowMatrix, bidList);
            Integer acceptedBidVolume = BidUtils.getTotalVolume(acceptedBidList);
            ConcurrentAssignmentPhaseUtils.removeLeastExpensiveFlows(sinkIndex, flowMatrix, acceptedBidVolume);
            ConcurrentAssignmentPhaseUtils.addFlowsForAcceptedBids(sinkIndex, flowMatrix, acceptedBidList);

            ConcurrentAssignmentPhaseUtils.assertThatNewVolumeIsCorrect(sinkIndex, flowMatrix);
        }
    }
}
