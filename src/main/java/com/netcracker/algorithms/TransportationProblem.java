package com.netcracker.algorithms;

import java.util.Arrays;

public class TransportationProblem {

    private final int[][] costMatrix;
    private final int[] supplyArray;
    private final int[] demandArray;

    public TransportationProblem(int[][] costMatrix,
                                 int[] supplyArray,
                                 int[] demandArray) {
        this.costMatrix = costMatrix;
        this.supplyArray = supplyArray;
        this.demandArray = demandArray;
    }

    public int[][] getCostMatrix() {
        return costMatrix;
    }

    public int[] getSupplyArray() {
        return supplyArray;
    }

    public int[] getDemandArray() {
        return demandArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportationProblem)) return false;
        TransportationProblem that = (TransportationProblem) o;
        return Arrays.equals(getCostMatrix(), that.getCostMatrix()) &&
                Arrays.equals(getSupplyArray(), that.getSupplyArray()) &&
                Arrays.equals(getDemandArray(), that.getDemandArray());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getCostMatrix());
        result = 31 * result + Arrays.hashCode(getSupplyArray());
        result = 31 * result + Arrays.hashCode(getDemandArray());
        return result;
    }
}
