package com.netcracker.algorithms.auction.entities;

import java.util.Objects;

public class Bid {

    private final int bidderSourceIndex;

    private final int ownerSourceIndex;
    private final int sinkIndex;
    private final double volume;
    private final double bidValue;

    public Bid(int bidderSourceIndex,
               int ownerSourceIndex,
               int sinkIndex,
               double volume,
               double bidValue) {
        this.bidderSourceIndex = bidderSourceIndex;
        this.ownerSourceIndex = ownerSourceIndex;
        this.sinkIndex = sinkIndex;
        this.volume = volume;
        this.bidValue = bidValue;
    }

    public int getBidderSourceIndex() {
        return bidderSourceIndex;
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

    public Bid split(double requestedVolume) {
        return new Bid(bidderSourceIndex, ownerSourceIndex, sinkIndex, requestedVolume, bidValue);
    }

    @Override
    public String toString() {
        return "Bid{" +
                "bidderSourceIndex=" + bidderSourceIndex +
                ", sinkIndex=" + sinkIndex +
                ", volume=" + volume +
                ", bidValue=" + bidValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;
        Bid bid = (Bid) o;
        return getBidderSourceIndex() == bid.getBidderSourceIndex() &&
                getSinkIndex() == bid.getSinkIndex() &&
                Double.compare(bid.getVolume(), getVolume()) == 0 &&
                Double.compare(bid.getBidValue(), getBidValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBidderSourceIndex(), getSinkIndex(), getVolume(), getBidValue());
    }
}
