package com.common.collect.util;

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

}
