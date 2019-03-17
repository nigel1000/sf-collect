package com.common.collect.container.mybatis;

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
}
