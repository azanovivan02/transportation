package com.netcracker.algorithms.auction.entities;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.emptyList;

public class BidMap {

    private final Map<Integer, List<Bid>> bidMap;

    public BidMap() {
        this.bidMap = new HashMap<>();
    }

    public void add(Bid bid) {
        int sinkIndex = bid.getSinkIndex();
        if(sinkIndex < 0){
            throw new IllegalStateException("Illegal sink index");
        }
        List<Bid> bidsForSink = bidMap.computeIfAbsent(sinkIndex, k -> new ArrayList<>());
        bidsForSink.add(bid);
    }

    public List<Bid> getBidsForSink(int sinkIndex) {
        List<Bid> bidsForSink = bidMap.get(sinkIndex);
        if (bidsForSink != null) {
            return bidsForSink;
        } else {
            return emptyList();
        }
    }

    public int size(){
        return bidMap
                .entrySet()
                .stream()
                .map(Entry::getValue)
                .map(List::size)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
