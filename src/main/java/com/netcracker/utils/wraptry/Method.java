package com.netcracker.utils.wraptry;

@FunctionalInterface
public interface Method<T> {

    T execute() throws Exception;
}
