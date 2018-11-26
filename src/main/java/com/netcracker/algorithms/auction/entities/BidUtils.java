package com.netcracker.algorithms.auction.entities;

import java.util.List;

public class BidUtils {

    public static Integer getTotalVolume(List<Bid> bidList) {
        return bidList
                .stream()
                .map(Bid::getVolume)
                .reduce(0, (total, volume) -> total += volume);
    }
}
