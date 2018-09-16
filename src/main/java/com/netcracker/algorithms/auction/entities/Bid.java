package com.netcracker.algorithms.auction.entities;

import java.util.Objects;

public class Bid {

    private final int sourceIndex;
    private final int sinkIndex;
    private final double volume;
    private final double bidValue;

    public Bid(int sourceIndex, int sinkIndex, double volume, double bidValue) {
        this.sourceIndex = sourceIndex;
        this.sinkIndex = sinkIndex;
        this.volume = volume;
        this.bidValue = bidValue;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public int getSinkIndex() {
        return sinkIndex;
    }

    public double getVolume() {
        return volume;
    }

    public double getBidValue() {
        return bidValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        Bid bid = (Bid) o;
        return getSourceIndex() == bid.getSourceIndex() &&
                getSinkIndex() == bid.getSinkIndex() &&
                Double.compare(bid.getVolume(), getVolume()) == 0 &&
                Double.compare(bid.getBidValue(), getBidValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceIndex(), getSinkIndex(), getVolume(), getBidValue());
    }
}
