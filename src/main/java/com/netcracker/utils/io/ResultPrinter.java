package com.netcracker.utils.io;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.utils.io.logging.Logger;
import com.netcracker.utils.io.logging.SystemOutLogger;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class ResultPrinter {

    private final static Logger logger = new SystemOutLogger(true);

    public static void printResults(Map<TransportationProblem, Map<String, Allocation>> allResults) {
        allResults.forEach((problem, allAllocationsForProblem) -> {
            int supplyArrayLength = problem.getSupplyArray().length;
            int demandArraylength = problem.getDemandArray().length;
            logger.info("Allocations for size: %d x %d", supplyArrayLength, demandArraylength);
            allAllocationsForProblem.forEach((solverName, allocation) -> {
                logger.info("=== %s ===", solverName);
                logger.info(allocation.toString());
            });
            logger.info("\n");
        });
    }
}
