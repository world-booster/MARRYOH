package com.grape.common.error;

import org.slf4j.MDC;

public class TraceId {

    private static final String KEY = "traceId";

    public static String get() {
        return MDC.get(KEY);
    }
}
