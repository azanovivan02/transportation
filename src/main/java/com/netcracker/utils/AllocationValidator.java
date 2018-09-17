package com.netcracker.utils;

import com.netcracker.algorithms.Allocation;

import java.util.Map;
import java.util.Map.Entry;

import static com.netcracker.utils.GeneralUtils.toLinkedMap;
import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;

public class AllocationValidator {

    public static void assertThatAllocationsAreIdentical(Map<String, Allocation> allocationMap,
                                                         int sourceAmount,
                                                         int sinkAmount) {
        Map<String, int[][]> volumeMatrixMap = getVolumeMatrixMap(allocationMap);

        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
                assertThatVolumeIsIdentical(volumeMatrixMap, sourceIndex, sinkIndex);
            }
        }
    }

    private static void assertThatVolumeIsIdentical(Map<String, int[][]> volumeMatrixMap,
                                                    int sourceIndex,
                                                    int sinkIndex) {
        String previousAllocationName = null;
        int previousVolume = 0;
        for (Entry<String, int[][]> entry : volumeMatrixMap.entrySet()) {
            String allocationName = entry.getKey();
            int[][] volumeMatrix = entry.getValue();
            int volume = volumeMatrix[sourceIndex][sinkIndex];
            if (previousAllocationName != null) {
                if (previousVolume != volume) {
                    String message = format(
                            "Allocations don't match. " +
                                    "For source = %d and sink = %d " +
                                    "allocation \"%s\" has volume %d, while " +
                                    "allocation \"%s\" has volume %d",
                            sourceIndex,
                            sinkIndex,
                            previousAllocationName,
                            previousVolume,
                            allocationName,
                            volume
                    );
                    throw new IllegalStateException(message);
                }
            }
            previousAllocationName = allocationName;
            previousVolume = volume;
        }
    }

    private static Map<String, int[][]> getVolumeMatrixMap(Map<String, Allocation> allocationMap) {
        return allocationMap
                .entrySet()
                .stream()
                .collect(
                        toLinkedMap(
                                Entry::getKey,
                                e -> e.getValue().getVolumeMatrix()
                        )
                );
    }
}
