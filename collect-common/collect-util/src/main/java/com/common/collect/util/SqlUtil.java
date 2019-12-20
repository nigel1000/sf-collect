package com.common.collect.util;

import lombok.NonNull;

import java.util.List;

/**
 * Created by fanxiaozhen on 2017/2/14.
 */
public class SqlUtil {

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

    public static String paging(String sortBy, Integer limit, Integer offset) {
        String sql = "";
        if (EmptyUtil.isNotBlank(sortBy)) {
            sql = " order by " + sortBy;
        }
        if (limit != null) {
            sql = " limit " + limit;
        }
        if (offset != null) {
            sql = " offset " + offset;
        }
        return sql;
    }

    public static String in(@NonNull String key, @NonNull List<?> args) {
        String sql = "";
        if (EmptyUtil.isEmpty(args)) {
            return sql;
        }
        int size = args.size() - 1;
        for (int i = 0; i < size + 1; i++) {
            if (i == 0) {
                sql = key + " in ( ";
            }
            sql = sql + "'" + args.get(i) + "'";
            if (size == i) {
                sql = sql + " ) ";
            } else {
                sql = sql + " , ";
            }
        }
        return sql;
    }

}