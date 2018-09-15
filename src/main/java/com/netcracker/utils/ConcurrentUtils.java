package com.netcracker.utils;

import java.util.List;
import java.util.concurrent.*;

import static com.netcracker.utils.wraptry.TryUtils.wrapIntoTry;
import static java.util.stream.Collectors.toList;

public class ConcurrentUtils {

    public static ExecutorService createExecutorService(int numberOfThreads) {
        return Executors.newFixedThreadPool(numberOfThreads);
    }

    public static void executeRunnableList(List<? extends Runnable> runnableList,
                                           ExecutorService executorService) {
        List<? extends Future<?>> futureList =
                runnableList
                        .stream()
                        .map(executorService::submit)
                        .collect(toList());
        for (Future future : futureList) {
            wrapIntoTry(future::get);
        }
    }

    public static <T> List<T> executeCallableList(List<? extends Callable<T>> callableList,
                                                  ExecutorService executorService) {
        try {
            return getResultList(executorService.invokeAll(callableList));
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> getResultList(List<Future<T>> futureList) {
        return futureList
                .stream()
                .map(future -> getResult(future))
                .collect(toList());
    }

    public static <T> T getResult(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int await(CyclicBarrier barrier) {
        return wrapIntoTry(barrier::await);
    }
}
