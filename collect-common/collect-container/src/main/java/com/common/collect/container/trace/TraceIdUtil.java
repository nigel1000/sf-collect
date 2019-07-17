package com.common.collect.container.trace;

import com.common.collect.util.IdUtil;
import org.slf4j.MDC;

import java.util.concurrent.Callable;

public class TraceIdUtil {

    public static String initTraceId() {
        String traceId = IdUtil.uuidHex();
        MDC.put(TraceConstants.TRACE_ID_KEY, traceId);
        return traceId;
    }

    public static String initTraceId(String traceId) {
        if (traceId == null) {
            return initTraceId();
        } else {
            MDC.put(TraceConstants.TRACE_ID_KEY, traceId);
            return traceId;
        }
    }

    public static void clearTraceId() {
        MDC.remove(TraceConstants.TRACE_ID_KEY);
    }

    public static String traceId() {
        return MDC.get(TraceConstants.TRACE_ID_KEY);
    }

    public static String initTraceIdIfAbsent() {
        String result = MDC.get(TraceConstants.TRACE_ID_KEY);
        if (result != null) {
            return result;
        }
        return initTraceId();
    }

    public static Runnable wrap(Runnable runnable) {
        String traceId = initTraceIdIfAbsent();
        return () -> {
            initTraceId(traceId);
            runnable.run();
            clearTraceId();
        };
    }

    public static <T> Callable<T> wrap(Callable<T> callable) {
        String traceId = initTraceIdIfAbsent();
        return () -> {
            initTraceId(traceId);
            T result = callable.call();
            clearTraceId();
            return result;
        };
    }

}
