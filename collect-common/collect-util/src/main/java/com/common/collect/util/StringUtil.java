package com.common.collect.util;

import lombok.NonNull;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/26.
 */

public class StringUtil {

    // split
    public static List<String> split2List(String str, @NonNull String separator) {
        if (EmptyUtil.isEmpty(str)) {
            return new ArrayList<>();
        }
        String[] strArray = str.split(ConvertUtil.escapeRegex(separator));
        return Arrays.asList(strArray);
    }

    // join
    public static String join(Collection<?> array, @NonNull String separator) {
        if (EmptyUtil.isEmpty(array)) {
            return "";
        }
        return join(array.toArray(), separator);
    }

    public static String join(Object[] array, @NonNull String separator) {
        int startIndex = 0;
        int endIndex = array.length;
        if (EmptyUtil.isEmpty(array)) {
            return "";
        }
        final StringBuilder buf = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static List<String> chars(final CharSequence cs) {
        List<String> charList = new ArrayList<>();
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return charList;
        }
        for (int i = 0; i < strLen; i++) {
            charList.add(String.valueOf(cs.charAt(i)));
        }
        return charList;
    }

    public static String format(String pattern, Object... args) {
        if (pattern == null || args == null || args.length == 0) {
            return pattern;
        }
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(pattern, args);
        String ret = formattingTuple.getMessage();
        if (formattingTuple.getThrowable() != null) {
            ret = ret + System.getProperty("line.separator") + fromException(formattingTuple.getThrowable());
        }
        return ret;
    }

    public static String fromException(Throwable ex) {
        if (ex == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
