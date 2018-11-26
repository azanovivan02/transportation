package com.netcracker.algorithms.auction.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

public class ConcurrentUtils {

    public static void executeRunnableList(List<Runnable> biddingRunnableList, int executorThreadAmount) {
        ExecutorService executor = Executors.newFixedThreadPool(executorThreadAmount);
        List<Future<?>> futureList = submitRunnableList(biddingRunnableList, executor);
        getFutureList(futureList);
        executor.shutdown();
    }

    public static void awaitBarrier(CyclicBarrier startBarrier) {
        try {
            startBarrier.await();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<Future<?>> submitRunnableList(List<Runnable> runnableList, ExecutorService executorService) {
          return runnableList
                  .stream()
                  .map(runnable-> executorService.submit(runnable))
                  .collect(toList());
    }

    public static List<Future<?>> submitRunnableSeveralTimes(Runnable runnable, int amount, ExecutorService executorService) {
        List<Future<?>> futureList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Future<?> future = executorService.submit(runnable);
            futureList.add(future);
        }
        return futureList;
    }

    public static void getFutureList(List<Future<?>> futureList) {
        for (Future<?> future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
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
