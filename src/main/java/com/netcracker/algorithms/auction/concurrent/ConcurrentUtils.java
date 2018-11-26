package com.netcracker.algorithms.auction.concurrent;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;

public class ConcurrentUtils {

    public static void awaitBarrier(CyclicBarrier startBarrier) {
        try {
            startBarrier.await();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T getFutureResult(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
