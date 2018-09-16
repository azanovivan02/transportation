package com.netcracker.algorithms;

import static com.netcracker.utils.GeneralUtils.intMatrixToString;
import static java.lang.String.format;

public class Allocation {

    private static final String NEW_LINE = "\n";

    private final TransportationProblem problem;
    private final int[][] allocationMatrix;
    private final int totalCost;

    public Allocation(TransportationProblem problem,
                      int[][] allocationMatrix) {
        this.problem = problem;
        this.allocationMatrix = allocationMatrix;
        this.totalCost = calculateTotalCost(
                problem.getCostMatrix(),
                allocationMatrix
        );
    }

    public TransportationProblem getProblem() {
        return problem;
    }

    public int[][] getAllocationMatrix() {
        return allocationMatrix;
    }

    public int getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total cost: ").append(totalCost).append(NEW_LINE);
        sb.append(intMatrixToString(allocationMatrix));
        return sb.toString();
    }

    private static int calculateTotalCost(int[][] costMatrix,
                                          int[][] allocationMatrix) {
        int totalCost = 0;

        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                totalCost += costMatrix[i][j] * allocationMatrix[i][j];
            }
        }

        return totalCost;
    }
}
