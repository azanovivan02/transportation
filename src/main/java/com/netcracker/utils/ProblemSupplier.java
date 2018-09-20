package com.netcracker.utils;

import com.netcracker.algorithms.TransportationProblem;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

public class ProblemSupplier {

    public static List<TransportationProblem> createProblemList() {
        return asList(
//                createSmallProblem()
//                createMediumProblem()
                createLargeProblem()
        );
    }

    private static TransportationProblem createSmallProblem() {
        int[][] costMatrix = new int[][]{
                {19, 30, 50, 12},
                {70, 30, 40, 60},
                {40, 10, 60, 20}
        };
        int[] sourceArray = new int[]{7, 10, 18};
        int[] sinkArray = new int[]{5, 8, 7, 15};
        return new TransportationProblem(costMatrix, sourceArray, sinkArray);
    }

    private static TransportationProblem createMediumProblem() {
        int[][] costMatrix = new int[][]{
                {46, 74, 9, 28, 99},
                {12, 75, 6, 36, 48},
                {35, 199, 4, 5, 71},
                {61, 81, 44, 88, 9},
                {85, 60, 14, 25, 79},
        };
        int[] sourceArray = new int[]{461, 277, 356, 488, 393};
        int[] sinkArray = new int[]{278, 60, 461, 116, 1060};
        return new TransportationProblem(costMatrix, sourceArray, sinkArray);
    }

    private static TransportationProblem createLargeProblem() {
        int[][] costMatrix = new int[][]{
                {54, 69, 94, 71, 61, 93, 12, 90, 55, 26},
                {85, 39, 66, 20, 17, 63, 38, 8, 74, 24},
                {13, 21, 18, 7, 71, 28, 55, 73, 1, 64},
                {48, 28, 29, 55, 34, 78, 76, 42, 88, 92},
                {8, 11, 29, 70, 89, 6, 93, 49, 16, 71},
                {71, 99, 76, 55, 10, 23, 1, 26, 86, 48},
                {90, 62, 27, 4, 62, 59, 5, 93, 67, 56},
                {34, 24, 96, 19, 29, 87, 70, 66, 51, 84},
                {34, 41, 13, 89, 69, 12, 70, 87, 73, 92},
                {19, 83, 63, 42, 89, 68, 78, 96, 19, 30},
        };
        int[] sourceArray = new int[]{28, 570, 973, 926, 24, 613, 270, 433, 153, 34};
        int[] sinkArray = new int[]{348, 65, 477, 493, 604, 313, 420, 161, 653, 718};
        return new TransportationProblem(costMatrix, sourceArray, sinkArray);
    }

    public static void main(String[] args) {
        TransportationProblem smallProblem = createMediumProblem();
        System.out.println(smallProblem.toJavaString());
    }
}
