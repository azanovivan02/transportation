package com.netcracker.utils;

import com.netcracker.algorithms.TransportationProblem;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;

public class ProblemSupplier {

    public static List<TransportationProblem> createProblemList() {
        int[][] costMatrix = new int[][]{
                {19, 30, 50, 12},
                {70, 30, 40, 60},
                {40, 10, 60, 20}
        };
        int[] sourceArray = new int[]{7, 10, 18};
        int[] sinkArray = new int[]{5, 8, 7, 15};
        TransportationProblem problem = new TransportationProblem(costMatrix, sourceArray, sinkArray);

        return singletonList(problem);
    }
}
