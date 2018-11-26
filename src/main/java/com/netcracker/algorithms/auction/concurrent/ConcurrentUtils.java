package com.netcracker.algorithms.auction.concurrent;

import java.util.concurrent.Future;

public class ConcurrentUtils {

    public static <T> T getFutureResult(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
