package com.common.collect.util;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/4/4.
 */
public class ThreadLocalUtil {

    private static Map<String, ThreadLocal<Object>> threadLocalMap = new ConcurrentHashMap<>();

    public static <T> T push(@NonNull String key, T value) {
        return push(key, () -> value);
    }

    public static <T> T push(@NonNull String key, Supplier<T> supplier) {
        ThreadLocal<Object> threadLocal = threadLocalMap.get(key);
        if (threadLocal == null) {
            threadLocal = new ThreadLocal<>();
        }
        T ret = supplier.get();
        threadLocal.set(ret);
        threadLocalMap.put(key, threadLocal);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T> T pull(@NonNull String key) {
        ThreadLocal<Object> threadLocal = threadLocalMap.get(key);
        if (threadLocal == null) {
            return null;
        }
        return (T) threadLocal.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T pullClear(@NonNull String key) {
        ThreadLocal<Object> threadLocal = threadLocalMap.get(key);
        if (threadLocal == null) {
            return null;
        }
        Object ret = threadLocal.get();
        threadLocal.remove();
        return (T) ret;
    }

    public static void clear(@NonNull String key) {
        ThreadLocal<Object> threadLocal = threadLocalMap.get(key);
        if (threadLocal == null) {
            return;
        }
        threadLocal.remove();
    }

}
