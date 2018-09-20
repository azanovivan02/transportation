package com.netcracker.algorithms.auction.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.removeLast;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static java.util.Comparator.comparingDouble;

public class FlowUtils {

    public static Integer getTotalVolume(List<Flow> flowList) {
        return flowList
                .stream()
                .map(Flow::getVolume)
                .reduce(0, (total, volume) -> total += volume);
    }

    public static void sortByValueAscending(List<Flow> flowList,
                                            int sourceIndex,
                                            int[][] benefitMatrix) {
        Comparator<Flow> profitComparator = comparingDouble(flow -> flow.getValue(sourceIndex, benefitMatrix));
        flowList.sort(profitComparator);
    }

    public static void sortByPriceDescending(List<Flow> flowList) {
        Comparator<Flow> reversedPriceComparator =
                comparingDouble(Flow::getPrice)
                        .reversed();
        flowList.sort(reversedPriceComparator);
    }

    public static List<Flow> getSublistWithTotalVolume(List<Flow> flowList,
                                                       int totalVolume) {
        List<Flow> addedFlows = new ArrayList<>();
        int addedVolume = 0;
        while (addedVolume < totalVolume) {
            Flow flow = removeLast(flowList);
            customAssert(flow != null, "flowList does not contain enough volume");
            int remainingVolume = totalVolume - addedVolume;
            double volume = flow.getVolume();
            if (volume <= remainingVolume) {
                addedVolume += volume;
                addedFlows.add(flow);
            } else {
                Flow splittedFlow = flow.split(remainingVolume);
                if (!addedFlows.isEmpty()) {
                    flowList.add(flow);
                }
                addedVolume += remainingVolume;
                addedFlows.add(splittedFlow);
            }
        }
        customAssert(doubleEquals(addedVolume, totalVolume));
        return addedFlows;
    }
}
