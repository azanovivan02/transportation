package com.netcracker.runners;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;

import java.util.List;
import java.util.Map;

import static com.netcracker.utils.AllocationValidator.assertThatAllocationsAreIdentical;
import static com.netcracker.utils.GeneralUtils.toLinkedMap;

public class RunnerUtils {

    public static Map<TransportationProblem, Map<String, Allocation>> findAllocationForEveryProblem(List<TransportationProblem> problemList,
                                                                                                    Map<String, TransportationProblemSolver> solverMap) {
        return problemList
                .stream()
                .collect(toLinkedMap(
                        problem -> problem,
                        problem -> findAllocationUsingMultipleSolvers(problem, solverMap)
                ));
    }

    //todo switch assertion AFTER printing
    public static Map<String, Allocation> findAllocationUsingMultipleSolvers(TransportationProblem problem,
                                                                             Map<String, TransportationProblemSolver> solverMap) {
        final Map<String, Allocation> allocationsForProblem = solverMap
                .entrySet()
                .stream()
                .collect(toLinkedMap(
                        Map.Entry::getKey,
                        solverEntry -> findAllocationUsingOneSolver(problem, solverEntry.getValue())
                ));

        int sourceAmount = problem.getSourceArray().length;
        int sinkAmount = problem.getSinkArray().length;
        assertThatAllocationsAreIdentical(
                allocationsForProblem,
                sourceAmount,
                sinkAmount
        );

        return allocationsForProblem;
    }

    public static Allocation findAllocationUsingOneSolver(TransportationProblem problem,
                                                          TransportationProblemSolver solver) {
        Allocation allocation = solver.findAllocation(problem);
//        final List<Integer> assignmentList = convertArrayToList(assignmentArray);
//        assert !containsDuplicates(assignmentList);
        return allocation;
    }
}
