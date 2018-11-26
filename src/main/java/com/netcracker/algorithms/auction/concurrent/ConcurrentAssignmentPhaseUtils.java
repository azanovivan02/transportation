package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowUtils;

import java.util.ArrayList;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.FlowUtils.getSublistWithTotalVolume;
import static com.netcracker.algorithms.auction.entities.FlowUtils.sortByPriceDescending;
import static com.netcracker.utils.GeneralUtils.prettyPrintList;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.lang.String.format;

public class ConcurrentAssignmentPhaseUtils {

    static void assertThatNewVolumeIsCorrect(int sinkIndex, ConcurrentFlowMatrix flowMatrix) {
        List<Flow> newFlowList = flowMatrix.getCurrentFlowListForSink(sinkIndex);
        int newTotalVolume = FlowUtils.getTotalVolume(newFlowList);

        int maxVolume = flowMatrix.getMaxVolumeForSink(sinkIndex);
        String message = format("Sink %d: new total volume = %d, max volume = %d", sinkIndex, newTotalVolume, maxVolume);
        customAssert(newTotalVolume == maxVolume, message);
    }

    static void addFlowsForAcceptedBids(int sinkIndex, ConcurrentFlowMatrix flowMatrix, List<Bid> acceptedBidList) {
        for (Bid bid : acceptedBidList) {
            info("Processing bid: %s", bid);
            int bidderSourceIndex = bid.getBidderSourceIndex();
            int bidVolume = bid.getVolume();
            double bidValue = bid.getBidValue();

            flowMatrix.increaseVolumeForFlow(bidderSourceIndex, sinkIndex, bidVolume);
            flowMatrix.setPriceForFlow(bidderSourceIndex, sinkIndex, bidValue);
        }
    }

    static void removeLeastExpensiveFlows(int sinkIndex, ConcurrentFlowMatrix flowMatrix, Integer acceptedBidVolume) {
        List<Flow> currentFlowList = flowMatrix.getCurrentFlowListForSink(sinkIndex);
        sortByPriceDescending(currentFlowList);

        info("Flows to sink %d by descending price:", sinkIndex);
        info(prettyPrintList(currentFlowList));

        List<Flow> leastExpensiveFlowList = getSublistWithTotalVolume(currentFlowList, acceptedBidVolume);
        if (!leastExpensiveFlowList.isEmpty()) {
            info("Least expensive flows from them, which have total volume %d:", acceptedBidVolume);
        }
        info(prettyPrintList(leastExpensiveFlowList));

        for (Flow flow : leastExpensiveFlowList) {
            flowMatrix.decreaseVolumeForFlow(
                    flow.getSourceIndex(),
                    sinkIndex,
                    flow.getVolume()
            );
        }
    }

    static List<Bid> chooseBidsToAccept(int sinkIndex, ConcurrentFlowMatrix flowMatrix, List<Bid> bidList) {
        int maxVolume = flowMatrix.getMaxVolumeForSink(sinkIndex);

        List<Bid> acceptedBidList = new ArrayList<>();
        int acceptedBidVolumeTemp = 0;
        while (acceptedBidVolumeTemp < maxVolume && !bidList.isEmpty()) {
            Bid bid = removeLast(bidList);
            int volume = bid.getVolume();

//            assertThatOwnerHasRequiredVolume(flowMatrix, sinkIndex, bid, volume);

            int remainingVolume = maxVolume - acceptedBidVolumeTemp;
            if (volume < remainingVolume) {
                acceptedBidList.add(bid);
                acceptedBidVolumeTemp += bid.getVolume();
            } else {
                Bid splittedBid = bid.split(remainingVolume);
                acceptedBidList.add(splittedBid);
                acceptedBidVolumeTemp += splittedBid.getVolume();
            }
        }

        String message = format("Sink %d accepted volume %d while it has capaity for %d and remaining bids %s", sinkIndex, acceptedBidVolumeTemp, maxVolume, bidList);
        customAssert((acceptedBidVolumeTemp == maxVolume) || (acceptedBidVolumeTemp < maxVolume && bidList.isEmpty()), message);
        if (acceptedBidList.isEmpty()) {
            info("Sink %d accepted no bids", sinkIndex);
        } else {
            info("Accepted bids for sink %d: ", sinkIndex);
            info(prettyPrintList(acceptedBidList));
        }
        info("Sink %d accepted bids to partial capacity (capacity=%d, bids=%s)", sinkIndex, maxVolume, acceptedBidList);
        info("We will clear space by removing least expensive flows to sink %d", sinkIndex);

        return acceptedBidList;
    }

//    static void assertThatOwnerHasRequiredVolume(ConcurrentFlowMatrix flowMatrix, int sinkIndex, Bid bid, int volume) {
//        int ownerSourceIndex = bid.getOwnerSourceIndex();
//        int currentlyOwnedVolume = flowMatrix.getFlow(ownerSourceIndex, sinkIndex).getVolume();
//        if (currentlyOwnedVolume < volume) {
//            int bidderSourceIndex = bid.getBidderSourceIndex();
//            String message = format(
//                    "Source %d attempts to buy volume %d to sink %d from source %d, but it owns only %d",
//                    bidderSourceIndex,
//                    volume,
//                    sinkIndex,
//                    ownerSourceIndex,
//                    currentlyOwnedVolume
//            );
//            throw new IllegalStateException(message);
//        }
//    }
}
