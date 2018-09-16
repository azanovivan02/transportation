package com.netcracker.utils.io.logging;

public class StaticLoggerHolder {

    private static final Logger logger = new SystemOutLogger(true);

    public static void info(String format, Object...args){
        logger.info(format, args);
    }

    private StaticLoggerHolder() {
    }
}
