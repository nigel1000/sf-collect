package com.common.collect.util;

/**
 * Created by fanxiaozhen on 2017/2/14.
 */
public class SqlUtil {

    private SqlUtil() {
    }

    public static String aroundLike(String column) {

        if (EmptyUtil.isBlank(column)) {
            return null;
        }
        return "%" + trim(column) + "%";
    }

    public static String tailLike(String column) {

        if (EmptyUtil.isBlank(column)) {
            return null;
        }
        return trim(column) + "%";
    }

    public static String headLike(String column) {

        if (EmptyUtil.isBlank(column)) {
            return null;
        }
        return "%" + trim(column);
    }


    public static String trim(String column) {

        if (EmptyUtil.isBlank(column)) {
            return null;
        }
        return column.trim();
    }
}