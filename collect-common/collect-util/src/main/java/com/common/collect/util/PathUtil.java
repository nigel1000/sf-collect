package com.common.collect.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hznijianfeng on 2018/8/25.
 */

public class PathUtil {

    public static boolean hasPathSpecial(String path) {
        if (path == null || "".equals(path.trim())) {
            return true;
        }
        if (path.contains("..") ||
                path.contains(File.separator + '.') ||
                path.contains('.' + File.separator) ||
                path.startsWith(".") ||
                path.endsWith(".") ||
                Pattern.compile(".*[<>&\"].*").matcher(path).matches()) {
            return true;
        }
        return false;
    }

    public static boolean hasFileSpecial(String fileName) {
        if (hasPathSpecial(fileName)) {
            return true;
        }
        if (Pattern.compile(".*[/\\\\].*").matcher(fileName).matches()) {
            return true;
        }
        return false;
    }

    public static String tailEndSeparator(String path) {
        return correctSeparator(path + File.separator);
    }

    // 多个/或者多个\缩减为一个
    public static String correctSeparator(String path) {
        if (path == null || "".equals(path.trim())) {
            return "";
        }
        List<Character> separator = Arrays.asList('\\', '/');
        StringBuilder ret = new StringBuilder();
        Character preStr = null;
        char[] chars = path.toCharArray();
        for (char one : chars) {
            if (preStr != null && separator.contains(one)) {
                continue;
            }
            if (separator.contains(one)) {
                preStr = one;
                ret.append(File.separator);
            } else {
                preStr = null;
                ret.append(one);
            }
        }
        return ret.toString();
    }

}
