package com.netcracker.algorithms.auction.single;

import com.netcracker.algorithms.auction.entities.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.BidUtils.getTotalVolume;
import static com.netcracker.algorithms.auction.entities.FlowUtils.*;
import static com.netcracker.utils.GeneralUtils.prettyPrintList;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.lang.String.format;
import static java.util.Comparator.comparingDouble;

public class AssignmentPhaseUtils {

    static void performSingleThreadedAssignmentPhase(FlowMatrix flowMatrix, BidMap bidMap, int sinkAmount) {
        Comparator<Bid> bidComparator = comparingDouble(Bid::getBidValue);
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            List<Bid> bidList = bidMap.getBidsForSink(sinkIndex);
            bidList.sort(bidComparator);
            performAssignmentPhaseForSingleSink(sinkIndex, flowMatrix, bidList);
        }
    }

    private static void performAssignmentPhaseForSingleSink(int sinkIndex, FlowMatrix flowMatrix, List<Bid> bidList) {
        info("\n=== Processing bids for sink %d ==============\n", sinkIndex);

        List<Bid> acceptedBidList = chooseBidsToAccept(sinkIndex, flowMatrix, bidList);
        Integer acceptedBidVolume = getTotalVolume(acceptedBidList);
        removeLeastExpensiveFlows(sinkIndex, flowMatrix, acceptedBidVolume);
        addFlowsForAcceptedBids(sinkIndex, flowMatrix, acceptedBidList);

        assertThatNewVolumeIsCorrect(sinkIndex, flowMatrix);
    }

    private static void assertThatNewVolumeIsCorrect(int sinkIndex, FlowMatrix flowMatrix) {
        List<Flow> newFlowList = flowMatrix.getCurrentFlowListForSink(sinkIndex);
        int newTotalVolume = FlowUtils.getTotalVolume(newFlowList);

        int maxVolume = flowMatrix.getMaxVolumeForSink(sinkIndex);
        String message = format("Sink %d: new total volume = %d, max volume = %d", sinkIndex, newTotalVolume, maxVolume);
        customAssert(newTotalVolume == maxVolume, message);
    }

    private static void addFlowsForAcceptedBids(int sinkIndex, FlowMatrix flowMatrix, List<Bid> acceptedBidList) {
        for (Bid bid : acceptedBidList) {
            info("Processing bid: %s", bid);
            int bidderSourceIndex = bid.getBidderSourceIndex();
            int bidVolume = bid.getVolume();
            double bidValue = bid.getBidValue();

            flowMatrix.increaseVolumeForFlow(bidderSourceIndex, sinkIndex, bidVolume);
            flowMatrix.setPriceForFlow(bidderSourceIndex, sinkIndex, bidValue);
        }
    }

    private static void removeLeastExpensiveFlows(int sinkIndex, FlowMatrix flowMatrix, Integer acceptedBidVolume) {
        List<Flow> currentFlowList = flowMatrix.getCurrentFlowListForSink(sinkIndex);
        sortByPriceDescending(currentFlowList);

        info("Flows to sink %d by descending price:", sinkIndex);
        info(prettyPrintList(currentFlowList));

        List<Flow> leastExpensiveFlowList = getHeadSublistWithTotalVolume(currentFlowList, acceptedBidVolume);
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

    private static List<Bid> chooseBidsToAccept(int sinkIndex, FlowMatrix flowMatrix, List<Bid> bidList) {
        int maxVolume = flowMatrix.getMaxVolumeForSink(sinkIndex);

        List<Bid> acceptedBidList = new ArrayList<>();
        int acceptedBidVolumeTemp = 0;
        while (acceptedBidVolumeTemp < maxVolume && !bidList.isEmpty()) {
            Bid bid = removeLast(bidList);
            int volume = bid.getVolume();

            assertThatOwnerHasRequiredVolume(flowMatrix, sinkIndex, bid, volume);

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

    private static void assertThatOwnerHasRequiredVolume(FlowMatrix flowMatrix, int sinkIndex, Bid bid, int volume) {
        int ownerSourceIndex = bid.getOwnerSourceIndex();
        int currentlyOwnedVolume = flowMatrix.getFlow(ownerSourceIndex, sinkIndex).getVolume();
        if (currentlyOwnedVolume < volume) {
            int bidderSourceIndex = bid.getBidderSourceIndex();
            String message = format(
                    "Source %d attempts to buy volume %d to sink %d from source %d, but it owns only %d",
                    bidderSourceIndex,
                    volume,
                    sinkIndex,
                    ownerSourceIndex,
                    currentlyOwnedVolume
            );
            throw new IllegalStateException(message);
        }
    }
}
