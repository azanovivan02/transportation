package com.netcracker.algorithms.auction.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.netcracker.utils.GeneralUtils.doubleEquals;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class FlowMatrix {

    public static final double UNUSED_PRICE = 0.0;
    public static final int UNUSED_SOURCE_INDEX = -1;

    private final int[] sourceArray;
    private final int[] sinkArray;

    private final double[][] volumeMatrix;
    private final double[][] priceMatrix;

    private final double[] unusedVolumeArray;
    private final int sourceAmount;
    private final int sinkAmount;

    public FlowMatrix(int[] sourceArray,
                      int[] sinkArray) {
        this.sourceArray = sourceArray;
        this.sinkArray = sinkArray;

        this.sourceAmount = sourceArray.length;
        this.sinkAmount = sinkArray.length;

        this.volumeMatrix = new double[sourceAmount][sinkAmount];
        this.priceMatrix = new double[sourceAmount][sinkAmount];

        this.unusedVolumeArray = createUnusedVolumeArray(sinkArray);
    }

    public Flow getFlow(int sourceIndex,
                        int sinkIndex) {
        double volume = volumeMatrix[sourceIndex][sinkIndex];
        double price = priceMatrix[sourceIndex][sinkIndex];
        return new Flow(
                sourceIndex,
                sinkIndex,
                volume,
                price
        );
    }

    public void setVolumeForFlow(int sourceIndex,
                                 int sinkIndex,
                                 double newVolume) {
        volumeMatrix[sourceIndex][sinkIndex] = newVolume;
    }

    public void setPriceForFlow(int sourceIndex,
                                int sinkIndex,
                                double newPrice) {
        double oldPrice = priceMatrix[sourceIndex][sinkIndex];
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
        double unusedVolume = unusedVolumeArray[sinkIndex];
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
                volumeMatrix[i][j] = 0.0;
            }
        }
    }

    public void resetUnusedFlowArray() {
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            double totalVolume = sinkArray[sinkIndex];
            double usedVolume = 0.0;
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
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < volumeMatrix.length; i++) {
            for (int j = 0; j < volumeMatrix[i].length; j++) {
                String entry = format("%3.1f ", volumeMatrix[i][j]);
                sb.append(entry);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static double[] createUnusedVolumeArray(int[] sinkArray) {
        double[] unusedVolumeArray = new double[sinkArray.length];
        for (int j = 0; j < sinkArray.length; j++) {
            unusedVolumeArray[j] = sinkArray[j];
        }
        return unusedVolumeArray;
    }
}
