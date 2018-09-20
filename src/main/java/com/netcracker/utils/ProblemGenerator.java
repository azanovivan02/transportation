package com.netcracker.utils;

import com.netcracker.algorithms.TransportationProblem;

import java.util.Random;

public class ProblemGenerator {

    public final static int DEFAULT_MAX_BENEFIT = 100;
    public final static int DEFAULT_SOURCE_VOLUME = 1000;
    public final static int DEFAULT_SINK_VOLUME = 1000;

    private final static Random random = new Random();

    public static void main(String[] args) {
        TransportationProblem problem = generateProblem(100, 100);
        System.out.println(problem.toJavaString());
    }

    public static TransportationProblem generateProblem(int sourceAmount,
                                                        int sinkAmount) {
        return generateProblem(
                sourceAmount,
                sinkAmount,
                DEFAULT_MAX_BENEFIT,
                DEFAULT_SOURCE_VOLUME,
                DEFAULT_SINK_VOLUME
        );
    }

    public static TransportationProblem generateProblem(int sourceAmount,
                                                        int sinkAmount,
                                                        int maxBenefit,
                                                        int maxSourceVolume,
                                                        int maxSinkVolume) {
        TransportationProblem problem = null;
        while (problem == null) {
            problem = tryToGenerateProblem(sourceAmount, sinkAmount, maxBenefit, maxSourceVolume, maxSinkVolume);
        }
        return problem;
    }

    private static TransportationProblem tryToGenerateProblem(int sourceAmount,
                                                              int sinkAmount,
                                                              int maxBenefit,
                                                              int maxSourceVolume,
                                                              int maxSinkVolume) {
        int[][] costMatrix = generateBenefitMatrix(
                sourceAmount,
                sinkAmount,
                maxBenefit
        );

        //todo find ways to always generate the correct problem
        int[] sinkArray = new int[sinkAmount];
        int totalSinkVolume = 0;
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            int randomSinkVolume = random.nextInt(maxSinkVolume);
            sinkArray[sinkIndex] = randomSinkVolume;
            totalSinkVolume += sinkArray[sinkIndex];
        }

        int[] sourceArray = new int[sourceAmount];
        int totalSourceVolume = 0;
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            int randomSourceVolume = random.nextInt(maxSourceVolume);
            sourceArray[sourceIndex] = randomSourceVolume;
            totalSourceVolume += sourceArray[sourceIndex];
        }

        if (totalSinkVolume > totalSourceVolume) {
            return new TransportationProblem(costMatrix, sourceArray, sinkArray);
        } else {
            return null;
        }
    }

    private static int[][] generateBenefitMatrix(int sourceAmount,
                                                 int sinkAmount,
                                                 int maxBenefit) {
        int[][] benefitMatrix = new int[sourceAmount][sinkAmount];
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
                int randomBenefit = random.nextInt(maxBenefit);
                benefitMatrix[sourceIndex][sinkIndex] = randomBenefit;
            }
        }
        return benefitMatrix;
    }
}
