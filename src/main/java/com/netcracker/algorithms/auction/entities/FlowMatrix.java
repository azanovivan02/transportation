package com.netcracker.algorithms.auction.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.intMatrixToString;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.stream.Collectors.toList;

public class FlowMatrix {

    public static final double UNUSED_PRICE = 0.0;
    public static final int UNUSED_SOURCE_INDEX = -1;

    private final int[] sourceArray;
    private final int[] sinkArray;

    private final int[][] volumeMatrix;
    private final double[][] priceMatrix;

    private final int[] unusedVolumeArray;
    private final int sourceAmount;
    private final int sinkAmount;

    public FlowMatrix(int[] sourceArray,
                      int[] sinkArray) {
        this.sourceArray = sourceArray;
        this.sinkArray = sinkArray;

        this.sourceAmount = sourceArray.length;
        this.sinkAmount = sinkArray.length;

        this.volumeMatrix = new int[sourceAmount][sinkAmount];
        this.priceMatrix = new double[sourceAmount][sinkAmount];

        this.unusedVolumeArray = createUnusedVolumeArray(sinkArray);
    }

    public Flow getFlow(int sourceIndex,
                        int sinkIndex) {
        if (sourceIndex < 0) {
            int volume = unusedVolumeArray[sinkIndex];
            return new Flow(
                    sourceIndex,
                    sinkIndex,
                    volume,
                    UNUSED_PRICE
            );
        } else {
            int volume = volumeMatrix[sourceIndex][sinkIndex];
            double price = priceMatrix[sourceIndex][sinkIndex];
            return new Flow(
                    sourceIndex,
                    sinkIndex,
                    volume,
                    price
            );
        }
    }

    public int[][] getVolumeMatrix() {
        return volumeMatrix;
    }

    public double[][] getPriceMatrix() {
        return priceMatrix;
    }

    public void increaseVolumeForFlow(int sourceIndex,
                                      int sinkIndex,
                                      int volumeIncrease) {
        if (sourceIndex < 0) {
            throw new IllegalStateException("Can't increase volume for unused flow");
        }
        volumeMatrix[sourceIndex][sinkIndex] += volumeIncrease;
    }

    public void decreaseVolumeForFlow(int sourceIndex,
                                      int sinkIndex,
                                      int volumeDecrease) {
        if (sourceIndex < 0) {
            int newVolume = unusedVolumeArray[sinkIndex] - volumeDecrease;
            if (newVolume >= 0) {
                unusedVolumeArray[sinkIndex] = newVolume;
            } else {
                throw new IllegalStateException("Volume can't be negative");
            }
        } else {
            int newVolume = volumeMatrix[sourceIndex][sinkIndex] - volumeDecrease;
            if (newVolume >= 0) {
                volumeMatrix[sourceIndex][sinkIndex] = newVolume;
            } else {
                throw new IllegalStateException("Volume can't be negative");
            }
        }
    }

    public void setPriceForFlow(int sourceIndex,
                                int sinkIndex,
                                double newPrice) {
//        double oldPrice = priceMatrix[sourceIndex][sinkIndex];
        double oldPrice = Double.MIN_VALUE;
        if (newPrice >= oldPrice) {
            priceMatrix[sourceIndex][sinkIndex] = newPrice;
        } else {
            throw new IllegalStateException(
                    "New price has to be bigger" +
                            ". From source: " + sourceIndex +
                            ", to sink: " + sinkIndex +
                            ", old price:" + oldPrice +
                            ", new price: " + newPrice
            );
        }
    }

    public Flow getUnusedFlow(int sinkIndex) {
        int unusedVolume = unusedVolumeArray[sinkIndex];
        return new Flow(
                UNUSED_SOURCE_INDEX,
                sinkIndex,
                unusedVolume,
                UNUSED_PRICE
        );
    }

    public List<Flow> getCurrentFlowList(int sourceIndex) {
        return IntStream
                .range(0, sinkAmount)
                .boxed()
                .map(sinkIndex -> getFlow(sourceIndex, sinkIndex))
                .filter(Flow::isNotEmpty)
                .collect(toList());
    }

    public List<Flow> getAvailableFlowList(int sourceIndex) {
        List<Flow> flowList = new ArrayList<>();
        for (int i = 0; i < sourceAmount; i++) {
            if (i == sourceIndex) {
                continue;
            }
            for (int j = 0; j < sinkAmount; j++) {
                Flow flow = getFlow(i, j);
                if (!flow.isEmpty()) {
                    flowList.add(flow);
                }
            }
        }
        for (int j = 0; j < unusedVolumeArray.length; j++) {
            Flow flow = getUnusedFlow(j);
            if (!flow.isEmpty()) {
                flowList.add(flow);
            }
        }
        return flowList;
    }

    public void resetVolumeMatrix() {
        for (int i = 0; i < sourceAmount; i++) {
            for (int j = 0; j < sinkAmount; j++) {
                volumeMatrix[i][j] = 0;
            }
        }
    }

    public void resetUnusedFlowArray() {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            int totalVolume = sinkArray[sinkIndex];
            int usedVolume = 0;
            for (int sourseIndex = 0; sourseIndex < sourceAmount; sourseIndex++) {
                usedVolume += volumeMatrix[sourseIndex][sinkIndex];
            }
            unusedVolumeArray[sinkIndex] = totalVolume - usedVolume;
        }
    }

    public boolean isComplete() {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            double totalVolume = sinkArray[sinkIndex];
            double usedVolume = 0.0;
            for (int sourseIndex = 0; sourseIndex < sourceAmount; sourseIndex++) {
                usedVolume += volumeMatrix[sourseIndex][sinkIndex];
            }
            if (!doubleEquals(totalVolume, usedVolume)) {
                return false;
            }
        }
        return true;
    }

    public String volumeMatrixToString() {
        return intMatrixToString(volumeMatrix);
    }

    private static int[] createUnusedVolumeArray(int[] sinkArray) {
        int[] unusedVolumeArray = new int[sinkArray.length];
        arraycopy(sinkArray, 0, unusedVolumeArray, 0, sinkArray.length);
        return unusedVolumeArray;
    }
}
