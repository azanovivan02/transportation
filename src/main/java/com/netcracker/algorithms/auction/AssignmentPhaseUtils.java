package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.auction.entities.*;

import java.util.ArrayList;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.FlowUtils.*;
import static com.netcracker.utils.GeneralUtils.prettyPrintList;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.lang.String.format;
import static java.util.Comparator.comparingDouble;

public class AssignmentPhaseUtils {

    static void performAssignmentPhase(FlowMatrix flowMatrix, BidMap bidMap, int sinkAmount) {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            info("\n=== Processing bids for sink %d ==============\n", sinkIndex);
            List<Bid> acceptedBidList = new ArrayList<>();

            List<Bid> bidList = bidMap.getBidsForSink(sinkIndex);
            bidList.sort(comparingDouble(Bid::getBidValue));

            int acceptedBidVolume = 0;
            int maxVolume = flowMatrix.getMaxVolumeForSink(sinkIndex);
            while (acceptedBidVolume < maxVolume && !bidList.isEmpty()) {
                Bid bid = removeLast(bidList);
                int volume = bid.getVolume();

                int bidderSourceIndex = bid.getBidderSourceIndex();
                int ownerSourceIndex = bid.getOwnerSourceIndex();
                int currentlyOwnedVolume = flowMatrix.getFlow(ownerSourceIndex, sinkIndex).getVolume();
                if (currentlyOwnedVolume < volume) {
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

                int remainingVolume = maxVolume - acceptedBidVolume;
                if (volume < remainingVolume) {
                    acceptedBidList.add(bid);
                    acceptedBidVolume += bid.getVolume();
                } else {
                    Bid splittedBid = bid.split(remainingVolume);
                    acceptedBidList.add(splittedBid);
                    acceptedBidVolume += splittedBid.getVolume();
                }
            }

            String message = format("Sink %d accepted volume %d while it has capaity for %d and remaining bids %s", sinkIndex, acceptedBidVolume, maxVolume, bidList);
            customAssert((acceptedBidVolume == maxVolume) || (acceptedBidVolume < maxVolume && bidList.isEmpty()), message);

            if (acceptedBidList.isEmpty()) {
                info("Sink %d accepted no bids", sinkIndex);
            } else {
                info("Accepted bids for sink %d: ", sinkIndex);
                info(prettyPrintList(acceptedBidList));
            }

            if (false) {
//            if (acceptedBidVolume == maxVolume) {
//                info("Sink %d accepted bids to full capacity (capacity=%d, bids=%s)", sinkIndex, maxVolume, acceptedBidList);
//                info("We will clear space by removing all flows to sink %d", sinkIndex);
//                flowMatrix.resetFlowVolumeForSink(sinkIndex);
            } else {
                info("Sink %d accepted bids to partial capacity (capacity=%d, bids=%s)", sinkIndex, maxVolume, acceptedBidList);
                info("We will clear space by removing least expensive flows to sink %d", sinkIndex);

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

            for (Bid bid : acceptedBidList) {
                info("Processing bid: %s", bid);
                int bidderSourceIndex = bid.getBidderSourceIndex();
                int ownerSourceIndex = bid.getOwnerSourceIndex();
                int bidVolume = bid.getVolume();
                double bidValue = bid.getBidValue();

                flowMatrix.increaseVolumeForFlow(bidderSourceIndex, sinkIndex, bidVolume);

                flowMatrix.setPriceForFlow(bidderSourceIndex, sinkIndex, bidValue);
            }

            List<Flow> newFlowList = flowMatrix.getCurrentFlowListForSink(sinkIndex);
            int newTotalVolume = getTotalVolume(newFlowList);

            message = format("Sink %d: new total volume = %d, max volume = %d", sinkIndex, newTotalVolume, maxVolume);
            customAssert(newTotalVolume == maxVolume, message);
        }

    }
}
