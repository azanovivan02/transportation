package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.ArrayList;
import java.util.List;

import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.prettyPrintList;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;

public class AssignmentPhaseUtils {

    static void performAssignmentPhase(FlowMatrix flowMatrix, int[] sinkArray, BidMap bidMap) {
        List<Bid> acceptedBidList = new ArrayList<>();
        for (int sinkIndex = 0; sinkIndex < sinkArray.length; sinkIndex++) {
            List<Bid> bidList = bidMap.getBidsForSink(sinkIndex);
            bidList.sort(comparingDouble(Bid::getBidValue));

            double acceptedBidVolume = 0.0;
            double totalVolume = sinkArray[sinkIndex];
            while (acceptedBidVolume < totalVolume && !bidList.isEmpty()) {
                Bid bid = removeLast(bidList);
                double volume = bid.getVolume();
                double remainingVolume = totalVolume - acceptedBidVolume;
                if (volume < remainingVolume) {
                    acceptedBidList.add(bid);
                    acceptedBidVolume += bid.getVolume();
                } else {
                    Bid splittedBid = bid.split(remainingVolume);
                    acceptedBidList.add(splittedBid);
                    acceptedBidVolume += splittedBid.getVolume();
                }
            }
            customAssert(acceptedBidVolume < totalVolume || doubleEquals(acceptedBidVolume, totalVolume));
        }

        flowMatrix.resetVolumeMatrix();

        info("Accepted bids: ");
        prettyPrintList(acceptedBidList);

        for (Bid bid : acceptedBidList) {
            int bidderSourceIndex = bid.getBidderSourceIndex();
            int sinkIndex = bid.getSinkIndex();
            double volume = bid.getVolume();
            double bidValue = bid.getBidValue();

            flowMatrix.setVolumeForFlow(bidderSourceIndex, sinkIndex, volume);
            flowMatrix.setPriceForFlow(bidderSourceIndex, sinkIndex, bidValue);

        }
        flowMatrix.resetUnusedFlowArray();
    }
}
