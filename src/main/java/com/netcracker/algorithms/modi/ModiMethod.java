package com.netcracker.algorithms.modi;

import com.netcracker.algorithms.Allocation;
import com.netcracker.algorithms.TransportationProblem;
import com.netcracker.algorithms.TransportationProblemSolver;

import java.util.Arrays;

public class ModiMethod implements TransportationProblemSolver {

    /**
     * Method for solving transportation problem using modified distribution method
     *
     *
     * @param problem @return
     */
    @Override
    public Allocation findAllocation(TransportationProblem problem) {

        int[][] costMatrix = problem.getCostMatrix();
        int[] supplyArray = problem.getSupplyArray();
        int[] demandArray = problem.getDemandArray();

        int numRows = costMatrix.length;
        int numCols = costMatrix[0].length;

        /* Total Supply */

        int totalSupply = 0;
        for (int i = 0; i < numRows; i++) {
            totalSupply += supplyArray[i];
        }

        /* Total Demand */

        int totalDemand = 0;
        for (int j = 0; j < numCols; j++) {
            totalDemand += demandArray[j];
        }

        /* Finding  basic feasible solution */

        int allocationMatrix[][] = new int[numRows][numCols];
        applyNorthWestCornerRule(allocationMatrix, supplyArray, demandArray, totalSupply, totalDemand);
        int totalNumAllocations = getTotalNumAllocations(allocationMatrix, numRows, numCols);

        /* Launching main loop */

        Sign sign[][] = new Sign[numRows][numCols];
        LoopValue loop[][] = new LoopValue[numRows + 2][numCols + 2];

        int[] u = new int[numRows];
        int[] v = new int[numCols];

        int lc = 0;
        int rowNumOfMaximum = -1;
        int colNumOfMaximum = -1;

        outer:
        while (totalNumAllocations == numRows + numCols - 1) {
            /* FOR BASIC CELL (where allocationMatrix[i][j] != 0) ============================== */
            /* Counting the number of allocations in row & column */
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (allocationMatrix[i][j] != 0) {
                        u[i]++;
                    }
                }
            }
            for (int j = 0; j < numCols; j++) {
                for (int i = 0; i < numRows; i++) {
                    if (allocationMatrix[i][j] != 0) {
                        v[j]++;
                    }
                }
            }

            /* Selecting the row or column having max number of allocations */
            int maxAllocatedAmount = 0;
            MostAllocations mostAllocations = MostAllocations.NOWHERE;
            for (int i = 0; i < numRows; i++) {
                if (maxAllocatedAmount < u[i]) {
                    maxAllocatedAmount = u[i];
                    rowNumOfMaximum = i;
                    mostAllocations = MostAllocations.ROW;
                }
            }
            for (int j = 0; j < numCols; j++) {
                if (maxAllocatedAmount < v[j]) {
                    maxAllocatedAmount = v[j];
                    colNumOfMaximum = j;
                    mostAllocations = MostAllocations.COLUMN;
                }
            }
            clearArray(u);
            clearArray(v);

            /* Assigning value for u and v */
            switch (mostAllocations) {
                case ROW:
                    for (int j = 0; j < numCols; j++) {
                        if (allocationMatrix[rowNumOfMaximum][j] != 0) {
                            v[j] = costMatrix[rowNumOfMaximum][j];
                        }
                    }
                    for (int k = 1; k < numRows; k++) {
                        for (int i = 0; i < numRows; i++) {
                            for (int j = 0; j < numCols; j++) {
                                if (allocationMatrix[i][j] != 0 && v[j] != 0) {
                                    u[i] = costMatrix[i][j] - v[j];
                                }
                            }
                        }
                        for (int j = 0; j < numCols; j++) {
                            for (int i = 0; i < numRows; i++) {
                                if (allocationMatrix[i][j] != 0 && u[i] != 0) {
                                    v[j] = costMatrix[i][j] - u[i];
                                }
                            }
                        }
                    }
                    break;
                case COLUMN:
                    for (int i = 0; i < numRows; i++) {
                        if (allocationMatrix[i][colNumOfMaximum] != 0) {
                            u[i] = costMatrix[i][colNumOfMaximum];
                        }
                    }
                    for (int k = 0; k < numRows; k++) {
                        for (int j = 0; j < numCols; j++) {
                            for (int i = 0; i < numRows; i++) {
                                if (allocationMatrix[i][j] != 0 && u[i] != 0) {
                                    v[j] = costMatrix[i][j] - u[i];
                                }
                            }
                        }
                        for (int i = 0; i < numRows; i++) {
                            for (int j = 0; j < numCols; j++) {
                                if (allocationMatrix[i][j] != 0 && v[j] != 0) {
                                    u[i] = costMatrix[i][j] - v[j];
                                }
                            }
                        }
                    }
                    break;
            }

            /* FOR NON BASIC CELL (where allocationMatrix[i][j] == 0)  ============================== */
            int maxOpCost = 0;
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (allocationMatrix[i][j] == 0) {
                        int opCost = costMatrix[i][j] - (u[i] + v[j]);
                        if (maxOpCost > opCost) {
                            maxOpCost = opCost;
                            rowNumOfMaximum = i;
                            colNumOfMaximum = j;
                        }
                    }
                }
            }
            if (maxOpCost >= 0) {
                break outer;
            }

            /* Loop Formation  ============================== */
            /* Add all basic cells to loop */
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (allocationMatrix[i][j] != 0) {
                        loop[i][j] = LoopValue.INCLUDED;
                    } else {
                        loop[i][j] = LoopValue.EXCLUDED;
                    }
                    sign[i][j] = Sign.ABSENT;
                }
            }

            for (int k = 0; k < numRows; k++) {
                for (int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCols; j++) {
                        if (loop[i][j] == LoopValue.INCLUDED) {
                            lc++;
                        }
                    }
                    if (lc == 1 && i != rowNumOfMaximum) {
                        for (int j = 0; j < numCols; j++) {
                            loop[i][j] = LoopValue.EXCLUDED;
                        }
                    }
                    lc = 0;
                }

                lc = 0;
                for (int j = 0; j < numCols; j++) {
                    for (int i = 0; i < numRows; i++) {
                        if (loop[i][j] == LoopValue.INCLUDED) {
                            lc++;
                        }
                    }
                    if (lc == 1 && j != colNumOfMaximum) {
                        for (int i = 0; i < numRows; i++) {
                            loop[i][j] = LoopValue.EXCLUDED;
                        }
                    }
                    lc = 0;
                }
            }

            /* Assigning the Sign to cells */
            sign[rowNumOfMaximum][colNumOfMaximum] = Sign.PLUS;
            {
                int i = rowNumOfMaximum;
                for (int k = 1; k <= 3; k++) {
                    int j;
                    for (j = 0; j < numCols; j++) {
                        if (loop[i][j] == LoopValue.INCLUDED && sign[i][j] == Sign.ABSENT) {
                            sign[i][j] = Sign.MINUS;
                            break;
                        }
                    }
                    for (i = 0; i < numRows; i++) {
                        if (loop[i][j] == LoopValue.INCLUDED && sign[i][j] == Sign.ABSENT) {
                            sign[i][j] = Sign.PLUS;
                            break;
                        }
                    }
                }
            }

            /* Finding @ Value */
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (sign[i][j] == Sign.MINUS && min > allocationMatrix[i][j]) {
                        min = allocationMatrix[i][j];
                    }
                }
            }
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {
                    if (sign[i][j] == Sign.PLUS) {
                        allocationMatrix[i][j] += min;
                    } else if (sign[i][j] == Sign.MINUS) {
                        allocationMatrix[i][j] -= min;
                    }
                }
            }

            /* Counting total amount of allocations to see if we must continue */
            totalNumAllocations = getTotalNumAllocations(allocationMatrix, numRows, numCols);
        }
        /* End of While */

        return new Allocation(problem, allocationMatrix);
    }

    private static void applyNorthWestCornerRule(int[][] allocationMatrix, int[] s, int[] d, int totalSupply, int totalDemand) {
        int numRows = allocationMatrix.length;
        int numCols = allocationMatrix[0].length;

        int k = 0;
        int i = 0;
        int j = 0;
        while (k < (numRows + numCols) - 1) {
            if (s[i] > d[j]) {
                k++;
                allocationMatrix[i][j] = d[j];
                s[i] = s[i] - d[j];
                totalSupply -= d[j];
                totalDemand -= d[j];
                d[j] = 0;
                j++;
            } else if (s[i] < d[j]) {
                k++;
                allocationMatrix[i][j] = s[i];
                d[j] = d[j] - s[i];
                totalSupply -= s[i];
                totalDemand -= s[i];
                s[i] = 0;
                i++;
            } else {
                k++;
                allocationMatrix[i][j] = s[i];
                totalSupply -= s[i];
                totalDemand -= s[i];
                s[i] = 0;
                d[j] = 0;
                i++;
                j++;
            }
            if ((totalSupply == 0) && (totalDemand == 0)) {
                break;
            }
        }
    }

    private static void clearArray(int[] array) {
        Arrays.fill(array, 0);
    }

    private static int getTotalNumAllocations(int[][] allocationMatrix, int numRows, int numCols) {
        int totalNumAllocations = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (allocationMatrix[i][j] != 0) {
                    totalNumAllocations++;
                }
            }
        }

        return totalNumAllocations;
    }

    private enum Sign {
        PLUS, MINUS, ABSENT
    }

    private enum MostAllocations {
        NOWHERE, ROW, COLUMN
    }

    private enum LoopValue {
        EXCLUDED, INCLUDED
    }
}


