package com.netcracker.utils;

import com.netcracker.algorithms.TransportationProblem;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ProblemSupplier {

    public  static List<TransportationProblem> createFixedProblemList(){
        return asList(
                createSmallProblem(),
                createProblemFive()
//                createProblemTen()
//                createProblemTwenty()
//                createProblemFifty()
        );
    }

    public static List<TransportationProblem> createRandomProblemList(int problemAmount, int problemSize){
        List<TransportationProblem> problemList = new ArrayList<>();
        for (int i = 0; i < problemAmount; i++) {
            TransportationProblem problem = ProblemGenerator.generateProblem(problemSize, problemSize);
            problemList.add(problem);
        }
        return problemList;
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

    private static TransportationProblem createProblemFive() {
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

    private static TransportationProblem createProblemTen() {
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

    private static TransportationProblem createProblemTwenty() {
        int[][] costMatrix = new int[][]{
                {83, 5, 87, 75, 61, 0, 41, 74, 76, 57, 90, 0, 22, 11, 43, 21, 69, 28, 27, 46},
                {97, 16, 0, 98, 52, 91, 46, 14, 28, 42, 62, 6, 13, 2, 5, 16, 9, 27, 79, 76},
                {60, 32, 95, 57, 92, 41, 69, 50, 86, 79, 7, 53, 99, 31, 53, 18, 22, 85, 43, 66},
                {19, 44, 38, 59, 61, 91, 36, 31, 9, 64, 72, 19, 49, 37, 54, 17, 12, 48, 23, 19},
                {51, 28, 78, 29, 12, 58, 47, 35, 6, 7, 53, 3, 46, 26, 32, 33, 29, 70, 8, 8},
                {92, 66, 65, 2, 81, 49, 4, 41, 99, 39, 34, 60, 85, 44, 8, 61, 32, 89, 95, 68},
                {88, 52, 67, 54, 33, 65, 48, 13, 35, 5, 53, 51, 27, 2, 13, 31, 25, 1, 19, 11},
                {72, 11, 33, 48, 65, 59, 13, 44, 16, 66, 57, 2, 67, 1, 26, 21, 45, 12, 52, 66},
                {48, 13, 71, 24, 64, 35, 90, 42, 41, 56, 65, 96, 87, 2, 82, 56, 45, 57, 2, 56},
                {95, 3, 65, 32, 61, 50, 92, 81, 25, 12, 82, 57, 44, 69, 43, 3, 94, 45, 41, 72},
                {17, 37, 37, 40, 73, 15, 2, 51, 90, 74, 9, 39, 33, 67, 10, 91, 28, 23, 10, 54},
                {26, 3, 34, 78, 7, 35, 68, 17, 94, 56, 61, 96, 9, 39, 3, 20, 73, 14, 40, 90},
                {36, 89, 4, 87, 27, 71, 65, 90, 89, 84, 5, 65, 91, 59, 74, 79, 17, 97, 5, 12},
                {3, 65, 64, 39, 21, 71, 55, 82, 72, 59, 58, 20, 19, 83, 26, 83, 49, 10, 57, 22},
                {73, 56, 0, 44, 92, 79, 56, 19, 74, 32, 56, 29, 22, 59, 15, 5, 40, 61, 22, 83},
                {49, 55, 21, 62, 42, 56, 43, 75, 26, 40, 67, 50, 86, 29, 35, 92, 54, 52, 96, 72},
                {70, 88, 25, 48, 98, 78, 61, 61, 48, 48, 66, 68, 17, 97, 72, 11, 63, 44, 47, 82},
                {66, 33, 51, 23, 33, 41, 6, 98, 80, 85, 40, 51, 84, 36, 74, 38, 83, 34, 31, 11},
                {51, 42, 5, 11, 62, 78, 37, 28, 57, 90, 19, 74, 73, 10, 12, 32, 58, 16, 1, 32},
                {27, 94, 78, 87, 26, 42, 76, 37, 78, 20, 77, 13, 75, 61, 38, 74, 95, 36, 21, 24},
        };
        int[] sourceArray = new int[]{294, 271, 266, 480, 542, 250, 309, 591, 909, 221, 499, 504, 991, 399, 323, 314, 599, 425, 968, 572};
        int[] sinkArray = new int[]{999, 635, 221, 766, 312, 225, 671, 422, 857, 724, 349, 760, 174, 871, 666, 616, 78, 248, 696, 418};
        return new TransportationProblem(costMatrix, sourceArray, sinkArray);
    }

    private static TransportationProblem createProblemFifty() {
        int[][] costMatrix = new int[][]{
                {49, 45, 52, 72, 99, 53, 83, 69, 85, 57, 38, 39, 31, 34, 7, 84, 14, 10, 39, 2, 71, 31, 31, 48, 85, 12, 67, 58, 98, 32, 62, 92, 99, 48, 72, 87, 83, 49, 91, 22, 7, 56, 21, 16, 43, 88, 83, 22, 34, 70},
                {14, 74, 36, 30, 16, 19, 18, 45, 82, 95, 92, 71, 97, 63, 50, 13, 36, 9, 32, 25, 67, 13, 51, 45, 82, 94, 61, 89, 13, 67, 66, 20, 12, 74, 89, 67, 88, 49, 46, 14, 49, 38, 46, 60, 99, 66, 38, 74, 4, 93},
                {74, 83, 4, 72, 77, 3, 59, 99, 93, 62, 8, 22, 60, 24, 36, 99, 78, 8, 15, 11, 47, 94, 82, 23, 2, 86, 93, 73, 55, 95, 96, 92, 74, 72, 29, 96, 49, 10, 98, 80, 52, 23, 66, 15, 37, 19, 22, 57, 86, 62},
                {20, 35, 24, 58, 32, 73, 13, 7, 43, 49, 98, 0, 89, 42, 23, 56, 64, 66, 55, 91, 75, 20, 41, 44, 57, 38, 89, 24, 14, 24, 70, 73, 36, 63, 12, 65, 8, 49, 25, 65, 93, 4, 65, 82, 22, 26, 18, 47, 99, 73},
                {51, 93, 28, 72, 29, 64, 98, 99, 77, 56, 24, 63, 25, 89, 68, 42, 1, 67, 4, 54, 98, 1, 26, 28, 33, 69, 75, 69, 35, 45, 27, 69, 70, 49, 83, 40, 28, 13, 97, 6, 67, 6, 69, 7, 20, 53, 1, 48, 2, 49},
                {95, 65, 47, 35, 95, 12, 59, 71, 76, 43, 25, 57, 24, 78, 54, 14, 35, 28, 97, 98, 14, 8, 41, 23, 92, 87, 58, 19, 50, 73, 74, 6, 85, 30, 65, 71, 44, 88, 77, 54, 42, 55, 61, 61, 13, 64, 24, 80, 49, 75},
                {1, 6, 60, 76, 57, 13, 10, 52, 68, 54, 62, 10, 35, 15, 71, 40, 24, 65, 65, 76, 65, 34, 12, 14, 94, 44, 80, 84, 96, 28, 3, 26, 3, 63, 16, 33, 96, 55, 77, 70, 6, 49, 32, 93, 0, 55, 96, 45, 19, 41},
                {50, 93, 53, 86, 38, 16, 92, 97, 35, 32, 91, 97, 27, 72, 67, 91, 78, 33, 83, 98, 99, 9, 34, 22, 29, 36, 41, 2, 52, 82, 18, 15, 70, 61, 75, 90, 1, 0, 60, 41, 51, 66, 15, 92, 41, 78, 71, 23, 74, 32},
                {9, 17, 55, 24, 91, 39, 9, 34, 78, 89, 95, 22, 70, 26, 2, 90, 65, 87, 60, 83, 42, 85, 75, 93, 2, 16, 35, 92, 96, 55, 55, 48, 97, 69, 58, 59, 3, 65, 11, 43, 3, 79, 62, 75, 66, 84, 42, 99, 93, 16},
                {71, 76, 29, 31, 57, 69, 22, 85, 3, 85, 33, 87, 75, 94, 21, 24, 99, 64, 2, 32, 89, 3, 60, 13, 64, 33, 51, 7, 42, 16, 29, 74, 62, 17, 22, 68, 35, 14, 61, 16, 72, 66, 51, 71, 72, 71, 51, 1, 50, 5},
                {93, 48, 5, 18, 77, 18, 61, 6, 1, 37, 57, 73, 79, 84, 26, 15, 64, 92, 62, 1, 45, 48, 71, 79, 89, 39, 2, 86, 73, 97, 19, 63, 56, 65, 92, 77, 10, 74, 38, 8, 43, 92, 64, 71, 86, 62, 46, 53, 32, 87},
                {82, 36, 4, 50, 58, 91, 8, 99, 27, 99, 74, 15, 67, 81, 22, 35, 5, 90, 15, 26, 89, 23, 42, 37, 19, 36, 13, 37, 98, 85, 86, 65, 34, 85, 80, 85, 39, 99, 67, 42, 82, 82, 45, 60, 53, 16, 20, 94, 82, 23},
                {48, 21, 9, 77, 77, 20, 14, 58, 22, 1, 19, 12, 29, 73, 24, 39, 3, 85, 38, 19, 68, 82, 30, 51, 72, 75, 77, 68, 49, 53, 77, 87, 45, 52, 11, 96, 40, 89, 53, 16, 72, 20, 40, 2, 85, 25, 66, 14, 0, 75},
                {41, 62, 3, 97, 24, 79, 66, 20, 71, 56, 18, 98, 75, 18, 26, 86, 25, 56, 29, 48, 87, 30, 51, 96, 37, 2, 31, 15, 37, 50, 82, 47, 86, 33, 97, 73, 7, 60, 43, 80, 0, 77, 18, 23, 40, 80, 76, 84, 90, 91},
                {45, 45, 17, 4, 10, 73, 98, 61, 98, 85, 17, 87, 39, 51, 48, 16, 82, 68, 89, 20, 15, 2, 7, 99, 18, 82, 43, 54, 97, 72, 22, 63, 47, 84, 99, 98, 99, 26, 48, 98, 67, 69, 17, 46, 6, 21, 47, 94, 69, 99},
                {80, 74, 93, 34, 25, 37, 21, 37, 18, 82, 2, 33, 17, 12, 20, 80, 29, 93, 69, 27, 2, 10, 2, 84, 62, 85, 78, 33, 60, 28, 30, 62, 21, 70, 67, 33, 80, 84, 12, 93, 59, 74, 67, 64, 4, 75, 27, 39, 18, 15},
                {72, 84, 16, 45, 10, 2, 68, 66, 69, 26, 6, 5, 30, 81, 80, 12, 68, 71, 2, 60, 66, 14, 91, 1, 81, 8, 50, 56, 38, 71, 4, 82, 32, 91, 39, 10, 52, 15, 3, 32, 46, 80, 1, 71, 49, 84, 73, 72, 24, 14},
                {98, 0, 88, 4, 70, 7, 42, 38, 40, 31, 53, 90, 1, 45, 17, 69, 25, 43, 31, 87, 95, 55, 13, 44, 98, 36, 10, 99, 11, 59, 63, 1, 49, 45, 69, 67, 71, 64, 19, 24, 8, 63, 29, 90, 90, 96, 7, 60, 24, 21},
                {60, 94, 91, 99, 82, 9, 23, 28, 26, 50, 24, 40, 17, 85, 61, 46, 22, 3, 64, 76, 57, 45, 76, 49, 13, 60, 46, 79, 54, 63, 88, 75, 30, 8, 8, 97, 85, 63, 23, 52, 12, 0, 2, 38, 14, 96, 60, 29, 43, 16},
                {91, 4, 74, 2, 7, 9, 38, 20, 95, 15, 6, 79, 15, 3, 57, 57, 66, 39, 66, 15, 79, 12, 29, 47, 29, 95, 74, 54, 90, 24, 35, 5, 68, 14, 36, 50, 10, 62, 8, 54, 71, 34, 75, 83, 67, 78, 11, 88, 37, 79},
                {29, 45, 12, 72, 12, 41, 19, 19, 85, 29, 81, 3, 66, 36, 37, 8, 53, 98, 20, 25, 2, 14, 47, 14, 97, 24, 36, 67, 42, 7, 61, 12, 58, 55, 95, 43, 10, 31, 2, 88, 0, 52, 99, 87, 53, 71, 4, 85, 59, 33},
                {17, 65, 80, 12, 26, 86, 1, 40, 24, 35, 78, 16, 81, 54, 33, 76, 84, 14, 58, 66, 59, 35, 97, 32, 59, 52, 52, 52, 23, 69, 52, 87, 77, 71, 88, 87, 38, 26, 77, 2, 21, 58, 24, 32, 2, 45, 94, 61, 29, 88},
                {77, 76, 6, 49, 17, 35, 98, 58, 46, 81, 44, 33, 38, 9, 57, 59, 67, 17, 53, 94, 34, 20, 54, 79, 87, 52, 68, 74, 77, 16, 68, 82, 98, 74, 87, 5, 20, 37, 34, 84, 84, 99, 55, 19, 80, 6, 67, 29, 35, 5},
                {16, 47, 76, 94, 31, 23, 92, 90, 47, 95, 6, 92, 64, 72, 54, 72, 49, 47, 63, 57, 49, 39, 9, 82, 46, 81, 21, 6, 30, 96, 96, 86, 27, 67, 64, 60, 49, 6, 78, 79, 50, 38, 9, 30, 41, 98, 17, 22, 94, 23},
                {92, 39, 11, 2, 58, 6, 63, 5, 11, 74, 59, 41, 29, 45, 2, 72, 16, 83, 7, 27, 90, 83, 10, 66, 95, 65, 32, 31, 86, 15, 52, 5, 64, 56, 42, 15, 74, 51, 39, 1, 29, 58, 96, 79, 11, 38, 80, 5, 41, 7},
                {33, 99, 48, 25, 35, 99, 86, 10, 92, 40, 69, 73, 76, 13, 32, 55, 76, 55, 6, 21, 54, 40, 50, 1, 16, 95, 65, 18, 6, 46, 0, 30, 3, 90, 3, 79, 73, 5, 75, 88, 1, 60, 39, 83, 73, 88, 6, 92, 16, 93},
                {21, 51, 32, 39, 84, 7, 67, 41, 44, 19, 72, 35, 47, 70, 86, 83, 92, 60, 52, 68, 2, 23, 63, 20, 4, 16, 53, 97, 31, 96, 50, 35, 4, 71, 64, 12, 98, 84, 54, 20, 39, 98, 18, 52, 24, 19, 89, 27, 6, 11},
                {87, 48, 20, 20, 11, 36, 59, 77, 98, 99, 32, 14, 19, 18, 62, 23, 72, 4, 96, 61, 48, 69, 19, 90, 96, 97, 5, 56, 48, 54, 17, 22, 7, 22, 59, 66, 48, 91, 77, 95, 21, 56, 95, 63, 82, 9, 68, 21, 38, 4},
                {98, 2, 33, 12, 0, 13, 53, 43, 8, 57, 96, 19, 31, 71, 4, 37, 24, 26, 86, 86, 1, 52, 37, 32, 42, 82, 55, 41, 25, 23, 78, 79, 84, 94, 45, 52, 45, 30, 18, 37, 11, 47, 32, 67, 17, 39, 34, 25, 84, 95},
                {2, 41, 80, 45, 64, 89, 56, 52, 67, 30, 46, 46, 98, 37, 70, 69, 29, 99, 50, 16, 63, 63, 76, 1, 80, 69, 94, 83, 12, 86, 93, 54, 3, 47, 77, 26, 48, 44, 52, 87, 13, 19, 67, 82, 76, 26, 52, 12, 96, 42},
                {96, 53, 8, 21, 33, 0, 41, 47, 87, 53, 63, 40, 61, 33, 29, 85, 49, 59, 14, 50, 92, 10, 17, 50, 79, 71, 66, 50, 86, 18, 58, 80, 87, 43, 12, 95, 16, 79, 17, 55, 27, 18, 35, 79, 14, 75, 68, 43, 95, 64},
                {50, 46, 86, 62, 91, 4, 83, 69, 5, 22, 92, 27, 0, 19, 82, 23, 8, 9, 64, 14, 62, 41, 35, 45, 48, 56, 78, 49, 53, 36, 64, 45, 92, 76, 64, 66, 8, 8, 74, 72, 92, 80, 34, 24, 76, 83, 25, 50, 8, 12},
                {80, 86, 83, 98, 38, 8, 46, 18, 71, 81, 19, 37, 87, 85, 19, 88, 61, 35, 54, 4, 40, 32, 86, 2, 24, 60, 13, 70, 98, 14, 76, 42, 90, 51, 17, 62, 82, 42, 24, 65, 79, 31, 86, 14, 73, 58, 44, 46, 17, 71},
                {28, 68, 42, 37, 2, 71, 10, 16, 72, 78, 2, 4, 4, 45, 69, 95, 15, 9, 67, 23, 83, 29, 9, 58, 23, 70, 26, 18, 95, 19, 27, 97, 35, 66, 16, 30, 89, 3, 80, 27, 63, 95, 27, 80, 99, 92, 73, 16, 9, 58},
                {30, 62, 87, 2, 96, 30, 24, 90, 24, 95, 12, 37, 73, 6, 45, 32, 41, 89, 19, 10, 58, 73, 37, 39, 41, 49, 2, 67, 69, 66, 23, 70, 11, 4, 4, 44, 98, 44, 56, 94, 74, 27, 83, 66, 58, 89, 1, 54, 2, 96},
                {83, 89, 12, 79, 14, 55, 59, 57, 45, 15, 95, 72, 14, 40, 49, 2, 97, 3, 70, 27, 36, 37, 66, 95, 87, 17, 78, 87, 75, 90, 90, 24, 7, 82, 95, 1, 46, 54, 66, 23, 36, 82, 98, 82, 77, 1, 20, 34, 68, 40},
                {88, 31, 74, 36, 77, 43, 85, 6, 57, 47, 96, 58, 74, 5, 89, 23, 13, 53, 14, 46, 74, 50, 6, 86, 62, 33, 8, 69, 65, 13, 75, 30, 3, 37, 92, 27, 50, 7, 57, 9, 11, 45, 39, 74, 42, 68, 38, 15, 19, 73},
                {1, 60, 5, 32, 5, 26, 67, 40, 96, 84, 40, 31, 59, 81, 97, 60, 65, 1, 85, 19, 95, 18, 12, 58, 28, 92, 16, 6, 34, 14, 38, 41, 76, 76, 74, 63, 83, 74, 77, 3, 16, 61, 24, 96, 19, 49, 13, 57, 74, 71},
                {55, 27, 42, 78, 48, 17, 39, 89, 11, 26, 60, 20, 8, 20, 25, 13, 23, 12, 65, 46, 12, 3, 4, 36, 34, 96, 57, 90, 87, 39, 53, 21, 80, 34, 87, 40, 39, 39, 41, 36, 69, 63, 41, 14, 54, 66, 2, 16, 4, 7},
                {42, 54, 64, 3, 76, 55, 57, 67, 99, 65, 7, 47, 29, 38, 75, 74, 6, 51, 17, 3, 53, 15, 82, 21, 81, 10, 5, 96, 38, 64, 42, 69, 40, 62, 77, 28, 45, 20, 46, 88, 26, 16, 29, 64, 14, 71, 61, 74, 47, 37},
                {2, 9, 63, 59, 75, 8, 16, 38, 95, 29, 50, 55, 61, 4, 29, 3, 23, 25, 62, 84, 64, 73, 34, 97, 61, 94, 97, 67, 53, 65, 26, 89, 66, 70, 36, 36, 64, 19, 66, 4, 94, 45, 77, 95, 16, 8, 10, 84, 10, 35},
                {19, 38, 4, 88, 58, 31, 3, 2, 17, 51, 0, 4, 74, 40, 45, 93, 67, 73, 0, 19, 98, 38, 20, 5, 63, 68, 75, 79, 55, 37, 23, 45, 20, 67, 9, 27, 59, 78, 67, 83, 54, 19, 5, 12, 24, 40, 3, 91, 20, 11},
                {63, 37, 46, 44, 63, 45, 96, 71, 63, 72, 23, 42, 88, 55, 66, 19, 60, 8, 45, 70, 93, 57, 53, 27, 57, 83, 96, 83, 77, 79, 39, 97, 11, 95, 80, 56, 6, 20, 25, 52, 4, 34, 27, 87, 82, 86, 16, 69, 24, 12},
                {92, 51, 41, 69, 4, 2, 32, 18, 10, 68, 89, 21, 73, 25, 69, 54, 88, 51, 44, 42, 12, 54, 69, 23, 63, 16, 25, 85, 83, 38, 58, 19, 0, 2, 99, 71, 57, 33, 57, 17, 1, 19, 76, 86, 9, 32, 71, 98, 72, 6},
                {75, 59, 16, 19, 39, 61, 15, 54, 94, 72, 97, 2, 82, 29, 56, 6, 44, 86, 52, 55, 52, 8, 42, 92, 46, 65, 51, 85, 5, 31, 30, 20, 58, 72, 74, 80, 97, 10, 3, 71, 27, 4, 80, 68, 7, 2, 34, 2, 74, 50},
                {71, 89, 26, 26, 61, 35, 4, 3, 60, 90, 90, 61, 63, 12, 12, 37, 92, 0, 20, 92, 10, 21, 17, 65, 23, 69, 77, 26, 36, 36, 10, 87, 20, 47, 55, 37, 2, 88, 40, 31, 83, 0, 79, 4, 31, 65, 83, 28, 97, 45},
                {63, 84, 22, 35, 29, 61, 15, 97, 73, 33, 74, 87, 0, 58, 22, 32, 2, 96, 5, 40, 91, 26, 60, 54, 35, 12, 62, 72, 82, 28, 53, 20, 19, 46, 61, 21, 93, 75, 90, 91, 71, 82, 70, 56, 1, 83, 90, 93, 99, 62},
                {13, 9, 46, 11, 55, 6, 71, 84, 23, 82, 69, 2, 89, 60, 67, 34, 3, 67, 11, 52, 86, 80, 78, 63, 62, 69, 3, 65, 70, 54, 91, 8, 55, 5, 71, 78, 65, 85, 75, 46, 56, 56, 44, 40, 12, 74, 32, 92, 2, 74},
                {5, 94, 87, 64, 50, 2, 35, 92, 51, 56, 56, 67, 46, 63, 47, 51, 64, 19, 17, 54, 54, 12, 91, 64, 30, 18, 18, 10, 2, 80, 72, 16, 80, 44, 91, 46, 9, 41, 81, 68, 2, 52, 20, 7, 47, 13, 85, 28, 83, 68},
                {23, 95, 56, 71, 52, 4, 13, 50, 83, 31, 88, 77, 78, 43, 9, 27, 56, 75, 88, 74, 12, 12, 54, 66, 80, 42, 83, 39, 33, 98, 70, 98, 95, 77, 50, 43, 45, 43, 40, 3, 24, 82, 10, 81, 60, 58, 47, 30, 89, 75},
        };
        int[] sourceArray = new int[]{682, 779, 494, 161, 747, 605, 305, 806, 131, 628, 433, 462, 228, 118, 385, 123, 9, 186, 960, 193, 294, 95, 189, 737, 598, 722, 204, 988, 243, 542, 33, 870, 951, 483, 297, 972, 202, 373, 401, 443, 549, 936, 438, 767, 154, 93, 130, 100, 503, 396};
        int[] sinkArray = new int[]{206, 850, 499, 283, 249, 958, 278, 126, 117, 101, 426, 94, 188, 135, 846, 983, 712, 773, 860, 997, 603, 282, 472, 435, 961, 666, 394, 985, 285, 838, 267, 729, 192, 393, 236, 398, 129, 499, 377, 889, 374, 967, 13, 977, 384, 578, 719, 246, 389, 83};
        return new TransportationProblem(costMatrix, sourceArray, sinkArray);
    }


    public static void main(String[] args) {
        TransportationProblem smallProblem = createProblemFive();
        System.out.println(smallProblem.toJavaString());
    }
}
