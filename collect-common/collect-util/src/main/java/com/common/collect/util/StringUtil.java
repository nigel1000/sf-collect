package com.common.collect.util;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
        return oldString.replaceAll(escapeRegex(regex), escapeRegex(newString));
    }

    public static String format(String pattern, Object... args) {
        if (EmptyUtil.isEmpty(pattern) || args == null || args.length == 0) {
            return pattern;
        }
        try {
            FormattingTuple formattingTuple = MessageFormatter.arrayFormat(pattern, args);
            String ret = formattingTuple.getMessage();
            if (formattingTuple.getThrowable() != null) {
                ret = ret + System.getProperty("line.separator");
                StringWriter stringWriter = new StringWriter();
                formattingTuple.getThrowable().printStackTrace(new PrintWriter(stringWriter));
                ret = ret + stringWriter.toString();
            }
            return ret;
        } catch (Exception ex) {
            String ret = pattern;
            for (Object arg : args) {
                ret = ret.replaceFirst("\\{}", escapeRegex(String.valueOf(arg)));
            }
            return ret;
        }
    }

    // 转义正则特殊字符
    public static String escapeRegex(String keyword) {
        if (EmptyUtil.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

}
