package com.netcracker.algorithms.auction.concurrent;

import com.netcracker.algorithms.auction.entities.Bid;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StartBarrierAction implements Runnable {

    final Set<Bid> bidSet;
    final AtomicInteger currentSourceIndex;

    final ConcurrentFlowMatrix flowMatrix;
    final AtomicBoolean flowMatrixIsComplete;

    public StartBarrierAction(Set<Bid> bidSet, AtomicInteger currentSourceIndex, ConcurrentFlowMatrix flowMatrix, AtomicBoolean flowMatrixIsComplete) {
        this.bidSet = bidSet;
        this.currentSourceIndex = currentSourceIndex;
        this.flowMatrix = flowMatrix;
        this.flowMatrixIsComplete = flowMatrixIsComplete;
    }

    @Override
    public void run() {
        bidSet.clear();
        currentSourceIndex.set(-1);
        synchronized (flowMatrix) {
            if (flowMatrix.isComplete()) {
                flowMatrixIsComplete.set(true);
            }
        }
    }
}
