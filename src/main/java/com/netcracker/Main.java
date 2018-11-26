package com.netcracker;

import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.concurrent.ConcurrentAuctionAlgorithm;
import com.netcracker.algorithms.auction.single.SingleThreadedAuctionAlgorithm;
import com.netcracker.runners.ModiComparisonTestRunner;
import com.netcracker.runners.StressTestRunner;
import com.netcracker.runners.TestRunner;

import java.util.List;

import static java.util.Arrays.asList;

public class Main {

    public static void main(String[] args) {
        List<TestRunner> runners = asList(
                new ModiComparisonTestRunner(),
                new StressTestRunner()
        );

//        TransportationProblemSolver auctionAlgorithm = new SingleThreadedAuctionAlgorithm();
        TransportationProblemSolver auctionAlgorithm = new ConcurrentAuctionAlgorithm();

        for (TestRunner runner : runners) {
            runner.run(auctionAlgorithm);
        }
    }
}
