package com.common.collect.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by hznijianfeng on 2019/3/26.
 */

public class StringUtil {

    public static List<String> split2CharList(final CharSequence cs) {
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

    public static String replaceAll(String regex, String oldString, String newString) {
        if (regex == null || newString == null || oldString == null) {
            return oldString;
        }
        return oldString.replaceAll(regex, newString);
    }

    public static String format(String str, Object... args) {
        if (EmptyUtil.isEmpty(str) || args == null || args.length == 0) {
            return str;
        }
        String ret = str;
        for (Object arg : args) {
            ret = ret.replaceFirst("\\{}", Matcher.quoteReplacement(String.valueOf(arg)));
        }
        return ret;
    }

}
