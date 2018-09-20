package com.netcracker.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

public class GeneralUtils {

    public static final String NEWLINE = "\n";

    public static <T> List<T> flatten(List<List<T>> nestedList) {
        return nestedList
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static List<Integer> convertArrayToList(int[] array) {
        return Arrays
                .stream(array)
                .boxed()
                .collect(Collectors.toList());
    }

    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }

    public static boolean doubleEquals(double a,
                                       double b) {
        return abs(a - b) < 0.00001;
    }

    public static <T> T removeLast(List<T> list) {
        if (!list.isEmpty()) {
            return list.remove(list.size() - 1);
        } else {
            return null;
        }
    }

    public static <E> String prettyPrintList(List<E> list) {
        return prettyPrintList(list, "  - ");
    }

    public static <E> String prettyPrintList(List<E> list, String leadingPart) {
        StringBuilder sb = new StringBuilder();
        for (E elem : list) {
            sb.append(leadingPart).append(elem).append(NEWLINE);
        }
        return sb.toString();
    }

    @SafeVarargs
    public static <E> List<E> merge(List<E>... listArray) {
        List<E> mergedList = new ArrayList<>();
        for (List<E> list : listArray) {
            mergedList.addAll(list);
        }
        return unmodifiableList(mergedList);
    }

    public static String intMatrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                String entry = format("%3d ", matrix[i][j]);
                sb.append(entry);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
