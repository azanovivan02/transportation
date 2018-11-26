package com.netcracker.runners;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;
import com.netcracker.algorithms.modi.ModiMethod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.netcracker.runners.RunnerUtils.findAllocationForEveryProblem;
import static com.netcracker.utils.ProblemSupplier.createFixedProblemList;
import static com.netcracker.utils.io.ResultPrinter.printResults;

public class ModiComparisonTestRunner implements TestRunner {

    @Override
    public void run(TransportationProblemSolver auctionAlgorithm) {
        System.out.println("=== Regular test");

        Map<String, TransportationProblemSolver> solverMap = new LinkedHashMap<>();
        solverMap.put("Auction", auctionAlgorithm);
        solverMap.put("MODI", new ModiMethod());

        List<TransportationProblem> problemList = createFixedProblemList();
        Map<TransportationProblem, Map<String, Allocation>> allAllocations = findAllocationForEveryProblem(problemList, solverMap);
        printResults(allAllocations);
    }
}
