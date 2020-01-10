package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by hznijianfeng on 2020/1/9.
 */
@Slf4j
public class ExceptionUtil {

    public static String getStackTraceAsString(Throwable ex) {
        if (ex == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


    public static void reThrowException(@NonNull NoReturn supplier, @NonNull String tips) {
        try {
            supplier.get();
        } catch (Exception ex) {
            throw UnifiedException.gen(tips, ex);
        }
    }

    public static void eatException(@NonNull NoReturn supplier, String logContent) {
        try {
            supplier.get();
        } catch (Exception ex) {
            if (logContent != null) {
                log.error(logContent, ex);
            }
        }
    }

    public static <T> T reThrowException(@NonNull HasReturn<T> supplier, @NonNull String tips) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            throw UnifiedException.gen(tips, ex);
        }
    }

    public static <T> T eatException(@NonNull HasReturn<T> supplier, String logContent) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            if (logContent != null) {
                log.error(logContent, ex);
            }
            return null;
        }
    }

    @FunctionalInterface
    public interface HasReturn<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface NoReturn {
        void get() throws Exception;
    }


}
