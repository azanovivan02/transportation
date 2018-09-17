package com.netcracker.algorithms.auction;

import com.netcracker.algorithms.auction.entities.Bid;
import com.netcracker.algorithms.auction.entities.BidMap;
import com.netcracker.algorithms.auction.entities.Flow;
import com.netcracker.algorithms.auction.entities.FlowMatrix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.netcracker.algorithms.auction.entities.Flow.getVolume;
import static com.netcracker.utils.GeneralUtils.doubleEquals;
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
        int availableVolume = getAvailableVolume(totalVolume, currentFlowList);
        List<Flow> addedFlowList = getAddedFlowList(sourceIndex, availableFlowList, availableVolume, benefitMatrix);

//        List<Flow> desiredFlowList = merge(currentFlowList, addedFlowList);
        List<Flow> desiredFlowList = addedFlowList;

        customAssert(
                doubleEquals(getVolume(desiredFlowList), availableVolume)
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

    private static List<Flow> getAddedFlowList(int sourceIndex,
                                               List<Flow> availableFlowList,
                                               int availableVolume,
                                               int[][] benefitMatrix) {
        sortByValueAscending(availableFlowList, sourceIndex, benefitMatrix);
        verifyThatSortedByValue(availableFlowList, sourceIndex, benefitMatrix);
        List<Flow> addedFlows = new ArrayList<>();
        int addedVolume = 0;
        while (addedVolume < availableVolume) {
            Flow flow = removeLast(availableFlowList);
            customAssert(flow != null, "Source can't consume more than all available flows");
            int remainingVolume = availableVolume - addedVolume;
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
                                                int[][] benefitMatrix) {
        List<Double> flowValueList =
                flowList
                        .stream()
                        .map(flow -> flow.getValue(sourceIndex, benefitMatrix))
                        .collect(Collectors.toList());
        double previousValue = Integer.MIN_VALUE;
        for (Double value : flowValueList) {
//            if(value < 0){
//                throw new IllegalStateException("Flow list ocntains flow with negative value: " + flowValueList);
//            }
            if (value < previousValue) {
                throw new IllegalStateException("Flow list is not sorted: " + flowValueList);
            }
            previousValue = value;
        }
    }

    private static int getAvailableVolume(int totalVolume,
                                             List<Flow> currentFlowList) {
        int currentVolume = getVolume(currentFlowList);
        return totalVolume - currentVolume;
    }
}
