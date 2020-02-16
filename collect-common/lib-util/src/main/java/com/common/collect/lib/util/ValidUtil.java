package com.common.collect.lib.util;


import com.common.collect.lib.api.Constants;

import java.util.regex.Pattern;

public class ValidUtil {

    // 获取字符串的字符数，中文算两个字符
    public static Integer getCharLength(String str) {
        if (str == null) {
            return 0;
        }
        return str.replaceAll(Constants.CHINESE_REG_EX, "aa").length();
    }

    // 手机号码的正则表达式
    public static boolean isPhone(String str) {
        return match(str, "[1]{1}[3|4|5|7|8|9]{1}[0-9]{9}");
    }

    public static boolean isTel(String str) {
        return match(str, "^([\\d]{3}-)?([\\d]{3,4}-)?[\\d]{7,8}(-[\\d]{1,4})?$");
    }

    // 校验邮箱格式
    public static boolean isEmail(String email) {
        return match(email, "^([\\w-\\.]+)@[\\w-.]+(\\.?[a-zA-Z]{2,4}$)");
    }

    // 校验邮箱格式
    public static boolean isUrl(String email) {
        return match(email, "^(http|https)://.*");
    }

    // 0.1
    // 111.22
    // 102,112,222
    // 102,112,222.121123
    public static boolean isAmount(String number) {
        return match(number, "^([0]{1})|([1-9]{1}([0-9]*|[0-9]{0,2}(,[0-9]{3})*)([.]{1}[0-9]+)?)$");
    }

    public static boolean match(String str, String regexp) {
        if (str == null || "".equals(str.trim()) ||
                regexp == null || "".equals(regexp.trim())) {
            return false;
        }
        return Pattern.matches(regexp, str);
    }

    public static boolean ge(String idStr, Integer val) {
        try {
            Long id = Long.valueOf(idStr);
            if (val == null) {
                return true;
            }
            if (id >= val) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

}