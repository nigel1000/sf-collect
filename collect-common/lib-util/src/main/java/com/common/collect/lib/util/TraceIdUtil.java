package com.common.collect.lib.util;

import org.slf4j.MDC;

import java.util.concurrent.Callable;

public class TraceIdUtil {

    public static final String TRACE_ID_KEY = "traceId";

    public static String initTraceId(String traceId) {
        if (EmptyUtil.isBlank(traceId)) {
            traceId = IdUtil.uuidHex();
        }
        MDC.put(TraceIdUtil.TRACE_ID_KEY, traceId);
        return traceId;
    }

    public static String traceId() {
        return MDC.get(TraceIdUtil.TRACE_ID_KEY);
    }

    public static void clearTraceId() {
        MDC.remove(TraceIdUtil.TRACE_ID_KEY);
    }

    public static Runnable wrap(Runnable runnable) {
        String traceId = traceId();
        return () -> {
            initTraceId(traceId);
            try {
                runnable.run();
            } finally {
                clearTraceId();
            }
        };
    }

    public static <T> Callable<T> wrap(Callable<T> callable) {
        String traceId = traceId();
        return () -> {
            initTraceId(traceId);
            try {
                return callable.call();
            } finally {
                clearTraceId();
            }
        };
    }

}
