package com.netcracker.algorithms.auction.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static com.netcracker.utils.GeneralUtils.intMatrixToString;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;
import static java.awt.SystemColor.info;
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
        if (sourceIndex >= sourceAmount) {
            throw new IllegalArgumentException("Source " + sourceIndex + " does not exist");
        }
        if (sinkIndex >= sinkAmount) {
            throw new IllegalArgumentException("Sink " + sinkIndex + " does not exist");
        }
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
        if (sourceIndex >= 0) {
            int newVolume = volumeMatrix[sourceIndex][sinkIndex] - volumeDecrease;
            if (newVolume >= 0) {
                info("Decreasing flow (%d, %d) from %d to %d", sourceIndex, sinkIndex, volumeMatrix[sourceIndex][sinkIndex], newVolume);
                volumeMatrix[sourceIndex][sinkIndex] = newVolume;
            } else {
                String message = format("Attempted to reduce volume of the flow (%d, %d) by %d, while it holds only %d", sourceIndex, sinkIndex, volumeDecrease, volumeMatrix[sourceIndex][sinkIndex]);
                throw new IllegalStateException(message);
            }
        } else {
            int newVolume = unusedVolumeArray[sinkIndex] - volumeDecrease;
            if (newVolume >= 0) {
                info("Decreasing unused flow (%d) from %d to %d", sinkIndex, unusedVolumeArray[sinkIndex], newVolume);
                unusedVolumeArray[sinkIndex] = newVolume;
            } else {
                String message = format("Attempted to reduce volume of the unused flow (%d) by %d, while it holds only %d", sinkIndex, volumeDecrease, unusedVolumeArray[sinkIndex]);
                throw new IllegalStateException(message);
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

    public List<Flow> getCurrentFlowListForSource(int sourceIndex) {
        return IntStream
                .range(0, sinkAmount)
                .boxed()
                .map(sinkIndex -> getFlow(sourceIndex, sinkIndex))
                .filter(Flow::isNotEmpty)
                .collect(toList());
    }

    public List<Flow> getCurrentFlowListForSink(int sinkIndex) {
        List<Flow> usedFlowList =
                IntStream
                        .range(0, sourceAmount)
                        .boxed()
                        .map(sourceIndex -> getFlow(sourceIndex, sinkIndex))
                        .filter(Flow::isNotEmpty)
                        .collect(toList());

        Flow unusedFlow = getUnusedFlow(sinkIndex);

        usedFlowList.add(unusedFlow);
        return usedFlowList;
    }

    public int getMaxVolumeForSink(int sinkIndex) {
        return sinkArray[sinkIndex];
    }

    public List<Flow> getAvailableFlowListForSink(int sourceIndex) {
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

    public void resetFlowVolumeForSink(int sinkIndex) {
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            volumeMatrix[sourceIndex][sinkIndex] = 0;
        }
    }

    public boolean isComplete() {
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            int usedVolume = getUsedVolumeForSource(sourceIndex);
            int maxVolume = sourceArray[sourceIndex];
            if (maxVolume != usedVolume) {
                return false;
            }
        }
        return true;
    }

    private int getUsedVolumeForSource(int sourceIndex) {
        int usedVolume = 0;
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            usedVolume += volumeMatrix[sourceIndex][sinkIndex];
        }
        return usedVolume;
    }

    private int getUsedVolumeForSink(int sinkIndex) {
        int usedVolume = 0;
        for (int sourseIndex = 0; sourseIndex < sourceAmount; sourseIndex++) {
            usedVolume += volumeMatrix[sourseIndex][sinkIndex];
        }
        return usedVolume;
    }

    public void assertIsValid() {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            int maxVolume = sinkArray[sinkIndex];
            int usedVolume = getUsedVolumeForSink(sinkIndex);
            int unusedVolume = unusedVolumeArray[sinkIndex];
            int totalVolume = usedVolume + unusedVolume;
            if (totalVolume != maxVolume) {
                throw new IllegalStateException("Volume matrix is invalid");
            }
        }
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
