package com.netcracker.runners;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.auction.single.SingleThreadedAuctionAlgorithm;

import java.util.List;
import java.util.Map;

import static com.netcracker.runners.RunnerUtils.findAllocationForEveryProblem;
import static com.netcracker.utils.ProblemSupplier.createRandomProblemList;
import static java.util.Collections.singletonMap;

public class StressTestRunner implements TestRunner {

    @Override
    public void run(TransportationProblemSolver auctionAlgorithm) {
        System.out.println("=== Stress test");

        Map<String, TransportationProblemSolver> solverMap = singletonMap("Auction", auctionAlgorithm);

        List<TransportationProblem> problemList = createRandomProblemList(3, 50);
        Map<TransportationProblem, Map<String, Allocation>> allAllocations = findAllocationForEveryProblem(problemList, solverMap);
//        printResults(allAllocations);
    }
}
