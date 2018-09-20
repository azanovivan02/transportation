package com.netcracker.utils;

import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.AuctionAlgorithm;
import com.netcracker.algorithms.modi.ModiMethod;

import java.util.LinkedHashMap;
import java.util.Map;

public class SolverSupplier {

    public static Map<String, TransportationProblemSolver> createSolverMap() {
        Map<String, TransportationProblemSolver> solverMap = new LinkedHashMap<>();

        solverMap.put("Auction", new AuctionAlgorithm());
//        solverMap.put("MODI", new ModiMethod());

        return solverMap;
    }
}
