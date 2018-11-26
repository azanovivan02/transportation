package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.List;
import java.util.stream.Collectors;

import static com.netcracker.algorithms.auction.entities.FlowUtils.*;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

public class ConcurrentBiddingPhaseUtils {

    static Bid getBidForFlow(Flow desiredFlow,
                                     int sourceIndex,
                                     int[] benefitMatrix,
                                     double secondBestFlowValue,
                                     Double epsilon,
                                     Flow secondBestFlow) {
        int desiredFlowOwnerSourceIndex = desiredFlow.getSourceIndex();
        int desiredFlowSinkIndex = desiredFlow.getSinkIndex();
        int desiredFlowVolume = desiredFlow.getVolume();

        int desiredFlowBenefit = benefitMatrix[desiredFlowSinkIndex];
        double desiredFlowPrice = desiredFlow.getPrice();
        double desiredFlowValue = desiredFlowBenefit - desiredFlowPrice;

        double bidValue = desiredFlowPrice + (desiredFlowValue - secondBestFlowValue) + epsilon;

        info("Making bid: sourse = %d, sink = %d", sourceIndex, desiredFlowSinkIndex);
        info("  - Desired flow: %s", desiredFlow);
        info("  - Second best flow: %s", secondBestFlow);
        info("  - Desired flow benefit: %s", desiredFlowBenefit);
        info("  - Desired flow price %s", desiredFlowPrice);
        info("  - Desired flow value: %s", desiredFlowValue);
        info("  - Second best flow value: %s", secondBestFlowValue);
        info("  - Diff: %s", desiredFlowValue - secondBestFlowValue);
        info("  - Bid value %s", bidValue);

        return new Bid(
                sourceIndex,
                desiredFlowOwnerSourceIndex,
                desiredFlowSinkIndex,
                desiredFlowVolume,
                bidValue
        );
    }

    static List<Flow> getAddedFlowList(int sourceIndex,
                                               List<Flow> availableFlowList,
                                               int availableVolume,
                                               int[][] benefitMatrix) {
        sortByValueAscending(
                availableFlowList,
                sourceIndex,
                benefitMatrix
        );
        verifyThatSortedByValue(
                availableFlowList,
                sourceIndex,
                benefitMatrix
        );
        return getSublistWithTotalVolume(
                availableFlowList,
                availableVolume
        );
    }

    static void verifyThatSortedByValue(List<Flow> flowList,
                                                int sourceIndex,
                                                int[][] benefitMatrix) {
        List<Double> flowValueList =
                flowList
                        .stream()
                        .map(flow -> flow.getValue(sourceIndex, benefitMatrix))
                        .collect(Collectors.toList());
        double previousValue = Integer.MIN_VALUE;
        for (Double value : flowValueList) {
            if (value < previousValue) {
                throw new IllegalStateException("Flow list is not sorted: " + flowValueList);
            }
            previousValue = value;
        }
    }

    static int getAvailableVolume(int totalVolume,
                                          List<Flow> currentFlowList) {
        int currentVolume = getTotalVolume(currentFlowList);
        return totalVolume - currentVolume;
    }
}
