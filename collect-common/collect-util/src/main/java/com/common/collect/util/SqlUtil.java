package com.common.collect.util;

import lombok.NonNull;

import java.util.List;

/**
 * Created by hznijianfeng on 2017/2/14.
 */
public class SqlUtil {

    public static String aroundLike(String column) {
        if (EmptyUtil.isBlank(column)) {
            return "";
        }
        return "%" + column + "%";
    }

    public static String tailLike(String column) {
        if (EmptyUtil.isBlank(column)) {
            return "";
        }
        return column + "%";
    }

    public static String headLike(String column) {
        if (EmptyUtil.isBlank(column)) {
            return "";
        }
        return "%" + column;
    }

    public static String paging(String sortBy, Integer limit, Integer offset) {
        String sql = "";
        if (EmptyUtil.isNotBlank(sortBy)) {
            sql += " order by " + sortBy + " ";
        }
        if (limit != null) {
            sql += " limit " + limit + " ";
        }
        if (offset != null) {
            sql += " offset " + offset + " ";
        }
        return sql;
    }

    public static String in(@NonNull String field, @NonNull List<?> values) {
        if (EmptyUtil.isEmpty(values)) {
            return "";
        }
        String sql = "";
        int size = values.size() - 1;
        for (int i = 0; i < size + 1; i++) {
            if (i == 0) {
                sql = " " + field + " in (";
            }
            sql += "'" + values.get(i) + "'";
            if (size == i) {
                sql += ") ";
            } else {
                sql += " , ";
            }
        }
        return sql;
    }

    public static String where(@NonNull String sql,
                               @NonNull List<Object> args,
                               @NonNull String field,
                               Object value) {
        return where(sql, "and", "=", args, field, value);
    }

    // link and or
    // symbol != = > <
    public static String where(@NonNull String sql,
                               @NonNull String link,
                               @NonNull String symbol,
                               @NonNull List<Object> args,
                               @NonNull String field,
                               Object value) {
        if (value == null) {
            return "";
        }
        String where = " where ";
        if (sql.contains(where) && sql.split(where).length == 2) {
            args.add(value);
            return link + " " + field + symbol + "? ";
        } else {
            args.add(value);
            return " where " + field + symbol + "? ";
        }
    }


    public static String set(@NonNull String sql,
                             @NonNull List<Object> args,
                             @NonNull String field,
                             Object value) {
        if (value == null) {
            return "";
        }
        String set = " set ";
        if (sql.contains(set) && sql.split(set).length == 2) {
            args.add(value);
            return " , " + field + " = ? ";
        } else {
            args.add(value);
            return " set " + field + " = ? ";
        }
    }

    public static String concat(String... sqls) {
        if (sqls == null) {
            return "";
        }
        String result = "";
        for (String sql : sqls) {
            if (sql != null) {
                result = result.concat(" ").concat(sql).concat(" ");
            }
        }
        return result;
    }

}