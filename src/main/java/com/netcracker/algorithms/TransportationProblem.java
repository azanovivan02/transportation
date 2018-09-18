package com.netcracker.algorithms;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class TransportationProblem {

    private static final String NEWLINE = "\n";

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

    /**
     * Prints problem in the following format:
     * <p>
     * Benefit matrix is used instead of cost matrix.
     * <p>
     * 2	2	2	4	1000
     * 4	6	4	3	700
     * 3	2	1	0	900
     * 900	800	500	400
     * <p>
     * Rows - sources, cols - sinks
     *
     * @return
     */
    @Override
    public String toString() {
        int sourceAmount = sourceArray.length;
        int sinkAmount = sinkArray.length;
        int[][] benefitMatrix = convertToBenefitMatrix(costMatrix);

        StringBuilder sb = new StringBuilder();
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
                int cellValue = benefitMatrix[sourceIndex][sinkIndex];
                appendCell(sb, cellValue);
            }
            int value = sourceArray[sourceIndex];
            appendCell(sb, value);
            sb.append(NEWLINE);
        }
        for (int sinkIndex = 0; sinkIndex < sinkAmount; sinkIndex++) {
            appendCell(sb, sinkArray[sinkIndex]);
        }
        return sb.toString();
    }

    private void appendCell(StringBuilder sb, int value) {
        sb.append(format(" %4d", value));
    }

    /**
     * Prints problem in the following format:
     * <p>
     * cost matrix is used.
     * <p>
     *         int[][] costMatrix = new int[][]{
     *                 {46, 74, 9, 28, 99},
     *                 {12, 75, 6, 36, 48},
     *                 {35, 199, 4, 5, 71},
     *                 {61, 81, 44, 88, 9},
     *                 {85, 60, 14, 25, 79},
     *         };
     *         int[] sourceArray = new int[]{461, 277, 356, 488, 393};
     *         int[] sinkArray = new int[]{278, 60, 461, 116, 1060};
     *         return new TransportationProblem(costMatrix, sourceArray, sinkArray);
     * <p>
     * Used for pasting randomly generated problems into the java code of ProblemSupplier class
     *
     * @return
     */
    public String toJavaString() {
        int sourceAmount = sourceArray.length;

        StringBuilder sb = new StringBuilder();

        sb.append("        int[][] costMatrix = new int[][]{\n");
        for (int sourceIndex = 0; sourceIndex < sourceAmount; sourceIndex++) {
            String costMatrixLine = arrayToJavaString(costMatrix[sourceIndex]);
            sb.append("                ").append(costMatrixLine).append(",\n");
        }
        sb.append("        };\n");
        sb.append("        int[] sourceArray = new int[]").append(arrayToJavaString(sourceArray)).append(";\n");
        sb.append("        int[] sinkArray = new int[]").append(arrayToJavaString(sinkArray)).append(";\n");
        sb.append("        return new TransportationProblem(costMatrix, sourceArray, sinkArray);");

        return sb.toString();
    }

    private String arrayToJavaString(int[] array) {
        List<String> numberList = stream(array).boxed().map(Object::toString).collect(toList());
        String numberString = join(", ", numberList);
        return "{" + numberString + "}";
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

    public static int[][] convertToBenefitMatrix(int[][] costMatrix) {
        int sourceAmout = costMatrix.length;
        int sinkAmount = costMatrix[0].length;
        int maxValue = getMaxValue(costMatrix) + 1;
        int[][] benefitMatrix = new int[sourceAmout][sinkAmount];
        for (int i = 0; i < sourceAmout; i++) {
            for (int j = 0; j < sinkAmount; j++) {
                benefitMatrix[i][j] = maxValue - costMatrix[i][j];
            }
        }
        return benefitMatrix;
    }

    private static int getMaxValue(int[][] costMatrix) {
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < costMatrix.length; i++) {
            for (int j = 0; j < costMatrix[0].length; j++) {
                int currentValue = costMatrix[i][j];
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                }
            }
        }
        return maxValue;
    }
}
