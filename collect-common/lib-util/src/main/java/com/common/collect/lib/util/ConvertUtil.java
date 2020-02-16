package com.common.collect.lib.util;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by nijianfeng on 2018/8/18.
 */

@Slf4j
public class ConvertUtil {

    public static String underline2Camel(String underline) {
        if (EmptyUtil.isBlank(underline)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String[] words = underline.toLowerCase().split("_");
        for (String word : words) {
            if (result.length() == 0) {
                result.append(word);
            } else {
                result.append(firstUpper(word));
            }
        }
        return result.toString();
    }

    public static String camel2Underline(String camel) {
        if (EmptyUtil.isBlank(camel)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<String> words = StringUtil.chars(camel);
        for (int i = 0; i < words.size(); i++) {
            // 从1开始 第一个大写不做处理
            if (i != 0 && Character.isUpperCase(words.get(i).charAt(0))) {
                sb.append("_");
            }
            sb.append(words.get(i));
        }
        return sb.toString().toLowerCase();
    }

    public static String firstLower(String input) {
        if (EmptyUtil.isBlank(input)) {
            return "";
        }
        if (input.length() == 1) {
            return input.toLowerCase();
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String firstUpper(String input) {
        if (EmptyUtil.isBlank(input)) {
            return "";
        }
        if (input.length() == 1) {
            return input.toUpperCase();
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
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
