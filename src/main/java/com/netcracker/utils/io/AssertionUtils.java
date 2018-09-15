package com.netcracker.utils.io;

public class AssertionUtils {

    public static void customAssert(boolean result) {
        customAssert(result, "Assertion failed");
    }

    public static void customAssert(boolean result, String message) {
        if (!result) {
            throw new IllegalStateException(message);
        }
    }
}
