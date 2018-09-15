package com.netcracker.utils.wraptry;

public class TryUtils {

//    public static void wrapIntoTry(VoidMethod method) {
//        try {
//            method.execute();
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }

    public static <T> T wrapIntoTry(Method<T> method) {
        try {
            return method.execute();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
