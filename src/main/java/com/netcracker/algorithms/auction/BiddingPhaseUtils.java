package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.netcracker.algorithms.auction.entities.Flow.getVolume;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.merge;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.util.Comparator.comparingDouble;

public class BiddingPhaseUtils {

    static BidMap performBiddingPhase(FlowMatrix flowMatrix,
                                      int[][] benefitMatrix,
                                      int[] sourceArray,
                                      Double epsilon) {
        BidMap bidMap = new BidMap();
        for (int sourceIndex = 0; sourceIndex < sourceArray.length; sourceIndex++) {
            addBidsFromSource(
                    sourceIndex,
                    flowMatrix,
                    benefitMatrix,
                    sourceArray[sourceIndex],
                    epsilon,
                    bidMap
            );
        }
        return bidMap;
    }

    private static void addBidsFromSource(int sourceIndex,
                                          FlowMatrix flowMatrix,
                                          int[][] benefitMatrix,
                                          int totalVolume,
                                          Double epsilon,
                                          BidMap bidMap) {
        List<Flow> availableFlowList = flowMatrix.getAvailableFlowList(sourceIndex);
        List<Flow> currentFlowList = flowMatrix.getCurrentFlowList(sourceIndex);
        double availableVolume = getAvailableVolume(totalVolume, currentFlowList);
        List<Flow> addedFlowList = getAddedFlowList(sourceIndex, availableFlowList, availableVolume, benefitMatrix);

        List<Flow> desiredFlowList = merge(currentFlowList, addedFlowList);

        customAssert(
                doubleEquals(getVolume(desiredFlowList), totalVolume)
        );

        Flow secondBestFlow = removeLast(availableFlowList);
        double secondBestFlowPrice = secondBestFlow.getPrice();
        int secondBestFlowSinkIndex = secondBestFlow.getSinkIndex();
        double secondBestFlowValue = benefitMatrix[sourceIndex][secondBestFlowSinkIndex] - secondBestFlowPrice;

        for (Flow desiredFlow : desiredFlowList) {
            Bid bid = getBidForFlow(desiredFlow, sourceIndex, benefitMatrix[sourceIndex], secondBestFlowValue, epsilon, secondBestFlow);
            bidMap.add(bid);
        }
    }

    private static Bid getBidForFlow(Flow desiredFlow,
                                     int sourceIndex,
                                     int[] benefitMatrix,
                                     double secondBestFlowValue,
                                     Double epsilon,
                                     Flow secondBestFlow) {
        int desiredFlowOwnerSourceIndex = desiredFlow.getSourceIndex();
        int desiredFlowSinkIndex = desiredFlow.getSinkIndex();
        double desiredFlowVolume = desiredFlow.getVolume();

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

    private static List<Flow> getAddedFlowList(int sourceIndex,
                                               List<Flow> availableFlowList,
                                               double availableVolume,
                                               int[][] benefitMatrix) {
        sortByValueAscending(availableFlowList, sourceIndex, benefitMatrix);
        verifyThatSortedByValue(availableFlowList, sourceIndex, benefitMatrix);
        List<Flow> addedFlows = new ArrayList<>();
        double addedVolume = 0.0;
        while (addedVolume < availableVolume) {
            Flow flow = removeLast(availableFlowList);
            customAssert(flow != null, "Source can't consume more than all available flows");
            double remainingVolume = availableVolume - addedVolume;
            double volume = flow.getVolume();
            if (volume <= remainingVolume) {
                addedVolume += volume;
                addedFlows.add(flow);
            } else {
                Flow splittedFlow = flow.split(remainingVolume);
                if (!addedFlows.isEmpty()) {
                    availableFlowList.add(flow);
                }
                addedVolume += remainingVolume;
                addedFlows.add(splittedFlow);
            }
        }
        customAssert(doubleEquals(addedVolume, availableVolume));
        return addedFlows;
    }

    private static void sortByValueAscending(List<Flow> flowList,
                                             int sourceIndex,
                                             int[][] benefitMatrix) {
        Comparator<Flow> profitComparator = comparingDouble(flow -> flow.getValue(sourceIndex, benefitMatrix));
        flowList.sort(profitComparator);
    }

    private static void verifyThatSortedByValue(List<Flow> flowList,
                                                int sourceIndex,
                                                int[][] benefitMatrix){
        double previousFlowValue = -1.0;
        for(Flow flow : flowList){
            double currentFlowValue = flow.getValue(sourceIndex, benefitMatrix);
            if(currentFlowValue < previousFlowValue){
                throw new IllegalStateException("Flow list is not sorted");
            }
            previousFlowValue = currentFlowValue;
        }
    }

    private static double getAvailableVolume(double totalVolume,
                                             List<Flow> currentFlowList) {
        double currentVolume = getVolume(currentFlowList);
        return totalVolume - currentVolume;
    }
}
