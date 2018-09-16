package com.netcracker.algorithms;

import java.util.Arrays;

public class TransportationProblem {

    private final int[][] costMatrix;
    private final int[] sourceArray;
    private final int[] sinkArray;

    public TransportationProblem(int[][] costMatrix,
                                 int[] sourceArray,
                                 int[] sinkArray) {
        this.costMatrix = costMatrix;
        this.sourceArray = sourceArray;
        this.sinkArray = sinkArray;
    }

    public int[][] getCostMatrix() {
        return costMatrix;
    }

    public int[] getSourceArray() {
        return sourceArray;
    }

    public int[] getSinkArray() {
        return sinkArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportationProblem)) return false;
        TransportationProblem that = (TransportationProblem) o;
        return Arrays.equals(getCostMatrix(), that.getCostMatrix()) &&
                Arrays.equals(getSourceArray(), that.getSourceArray()) &&
                Arrays.equals(getSinkArray(), that.getSinkArray());
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getCostMatrix());
        result = 31 * result + Arrays.hashCode(getSourceArray());
        result = 31 * result + Arrays.hashCode(getSinkArray());
        return result;
    }
}
