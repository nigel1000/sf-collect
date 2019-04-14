package com.common.collect.container.mybatis;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

/**
 * Created by nijianfeng on 2019/3/17.
 */
public class MybatisContext {

    private static ThreadLocal<String> sqlRecord = new ThreadLocal<>();
    private static ThreadLocal<Boolean> enableSqlRecord = new ThreadLocal<>();

    public static void setEnableSqlRecord(boolean enable) {
        enableSqlRecord.set(enable);
    }

    public static String getSqlRecord(boolean needClear) {
        String sql = sqlRecord.get();
        if (needClear) {
            sqlRecord.remove();
            enableSqlRecord.remove();
        }
        return sql;
    }

    public static boolean getEnableSqlRecord() {
        return Optional.ofNullable(enableSqlRecord.get()).orElse(false);
    }

    public static void setSqlRecord(String sql) {
        sqlRecord.set(sql);
    }

    static List<String> logFilterKeys = Lists.newArrayList();

    public static void addLogFilterKey(String addKey, String delKey) {
        if (addKey != null) {
            logFilterKeys.add(addKey);
        }
        if (delKey != null) {
            logFilterKeys.remove(delKey);
        }
    }
}
