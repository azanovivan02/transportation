package com.netcracker.algorithms.auction.entities;

import java.util.Objects;

import static java.lang.Math.abs;

public class Flow {

    private final int sourceIndex;
    private final int sinkIndex;

    private final int volume;
    private final double price;

    public Flow(int sourceIndex,
                int sinkIndex,
                int volume,
                double price) {
        this.sourceIndex = sourceIndex;
        this.sinkIndex = sinkIndex;
        this.volume = volume;
        this.price = price;
    }

    public static Flow createEmptyFlow(int sourceIndex,
                                       int sinkIndex) {
        return new Flow(sourceIndex, sinkIndex, 0, 0.0);
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public int getSinkIndex() {
        return sinkIndex;
    }

    public int getVolume() {
        return volume;
    }

    public double getPrice() {
        return price;
    }

    public boolean isEmpty() {
        //todo replace with Double library method call
        return abs(volume) < 0.000001;
    }

    public boolean isNotEmpty() {
        //todo replace with Double library method call
        return !isEmpty();
    }

    public double getValue(int sourceIndex,
                           int[][] benefitMatrix) {
        int benefit = benefitMatrix[sourceIndex][sinkIndex];
        return getValue(benefit);
    }

    public double getValue(int benefit) {
        return benefit - price;
    }

    public Flow split(int requestedVolume) {
        return new Flow(sourceIndex, sinkIndex, requestedVolume, price);
    }

    @Override
    public String toString() {
        return "Flow{" +
                "sourceIndex=" + sourceIndex +
                ", sinkIndex=" + sinkIndex +
                ", volume=" + volume +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flow)) return false;
        Flow flow = (Flow) o;
        return getSourceIndex() == flow.getSourceIndex() &&
                getSinkIndex() == flow.getSinkIndex() &&
                Double.compare(flow.getVolume(), getVolume()) == 0 &&
                Double.compare(flow.getPrice(), getPrice()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceIndex(), getSinkIndex(), getVolume(), getPrice());
    }
}
