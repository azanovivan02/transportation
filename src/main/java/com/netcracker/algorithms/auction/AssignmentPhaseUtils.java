package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.ArrayList;
import java.util.List;

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

            int acceptedBidVolume = 0;
            int totalVolume = sinkArray[sinkIndex];
            while (acceptedBidVolume < totalVolume && !bidList.isEmpty()) {
                Bid bid = removeLast(bidList);
                int volume = bid.getVolume();

                int bidderSourceIndex = bid.getBidderSourceIndex();
                int ownerSourceIndex = bid.getOwnerSourceIndex();
                int currentlyOwnedVolume = flowMatrix.getFlow(ownerSourceIndex, sinkIndex).getVolume();
                if(currentlyOwnedVolume < volume){
                    String message = String.format(
                            "Source %d attempts to buy volume %d to sink %d from source %d, but it owns only %d",
                            bidderSourceIndex,
                            volume,
                            sinkIndex,
                            ownerSourceIndex,
                            currentlyOwnedVolume
                    );
                    throw new IllegalStateException(message);
                }

                int remainingVolume = totalVolume - acceptedBidVolume;
                if (volume < remainingVolume) {
                    acceptedBidList.add(bid);
                    acceptedBidVolume += bid.getVolume();
                } else {
                    Bid splittedBid = bid.split(remainingVolume);
                    acceptedBidList.add(splittedBid);
                    acceptedBidVolume += splittedBid.getVolume();
                }
            }
            customAssert(acceptedBidVolume <= totalVolume);
        }

//        flowMatrix.resetVolumeMatrix();

        info("Accepted bids: ");
        prettyPrintList(acceptedBidList);

        for (Bid bid : acceptedBidList) {
            int bidderSourceIndex = bid.getBidderSourceIndex();
            int ownerSourceIndex = bid.getOwnerSourceIndex();
            int sinkIndex = bid.getSinkIndex();
            int volume = bid.getVolume();
            double bidValue = bid.getBidValue();

//            flowMatrix.setVolumeForFlow(bidderSourceIndex, sinkIndex, volume);

            flowMatrix.increaseVolumeForFlow(bidderSourceIndex, sinkIndex, volume);
            flowMatrix.decreaseVolumeForFlow(ownerSourceIndex, sinkIndex, volume);

            flowMatrix.setPriceForFlow(bidderSourceIndex, sinkIndex, bidValue);

        }
//        flowMatrix.resetUnusedFlowArray();
    }
}
