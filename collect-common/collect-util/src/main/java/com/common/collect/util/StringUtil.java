package com.common.collect.util;

import lombok.NonNull;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by hznijianfeng on 2019/3/26.
 */

public class StringUtil {

    public static final String COMMA_SPECIAL = ",";

    // split
    public static String[] split2Array(String key, @NonNull String special) {
        if (EmptyUtil.isBlank(key)) {
            return new String[]{};
        }
        // 去空 去空格
        String[] keys = key.split(ConvertUtil.escapeRegex(special));
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            String temp = keys[i];
            if (EmptyUtil.isBlank(temp)) {
                continue;
            }
            keys[index] = temp;
            index++;
        }
        return Arrays.copyOfRange(keys, 0, index);
    }

    public static <T> List<T> split(String key, @NonNull String special, Function<String, T> valueFunc) {
        return FunctionUtil.valueList(Arrays.asList(split2Array(key, special)), valueFunc);
    }

    public static List<Long> split2LongByComma(String key) {
        return split(key, COMMA_SPECIAL, Long::valueOf);
    }

    public static List<String> split2StringByComma(String key) {
        return split(key, COMMA_SPECIAL, s -> s);
    }

    // join
    public static <T> String joinByComma(List<T> list) {
        return join(list, COMMA_SPECIAL);
    }

    public static <T> String join(List<T> keys, @NonNull String special) {
        if (keys == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (T key : keys) {
            // 去空
            if (key != null && !"".equals(key.toString())) {
                sb.append(key).append(special);
            }
        }
        String result = sb.toString();
        if (result.length() == 0) {
            return result;
        } else {
            return result.substring(0, result.length() - special.length());
        }
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
            ret = ret + System.getProperty("line.separator");
            StringWriter stringWriter = new StringWriter();
            formattingTuple.getThrowable().printStackTrace(new PrintWriter(stringWriter));
            ret = ret + stringWriter.toString();
        }
        return ret;
    }

}
