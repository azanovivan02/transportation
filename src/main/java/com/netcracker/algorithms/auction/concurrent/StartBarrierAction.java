package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBarrierAction implements Runnable {

    final Set<Bid> bidSet;
    final AtomicInteger currentSourceIndex;

    public StartBarrierAction(Set<Bid> bidSet, AtomicInteger currentSourceIndex) {
        this.bidSet = bidSet;
        this.currentSourceIndex = currentSourceIndex;
    }

    @Override
    public void run() {
        bidSet.clear();
        currentSourceIndex.set(-1);
    }
}
