package com.common.collect.tool.mybatis;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/3/17.
 */
public class MybatisContext {

    private static ThreadLocal<List<String>> sqlRecord = new ThreadLocal<>();
    private static ThreadLocal<Boolean> enableSqlRecord = new ThreadLocal<>();

    public static void setEnableSqlRecord(boolean enable) {
        enableSqlRecord.set(enable);
    }

    public static List<String> getSqlRecord(boolean needClear) {
        List<String> sqls = sqlRecord.get();
        if (sqls == null) {
            sqls = new ArrayList<>();
        }
        if (needClear) {
            sqlRecord.remove();
            enableSqlRecord.remove();
        }
        return sqls;
    }

    public static boolean getEnableSqlRecord() {
        Boolean sqlRecord = enableSqlRecord.get();
        if (sqlRecord == null) {
            return false;
        }
        return sqlRecord;
    }

    public static void setSqlRecord(String sql) {
        List<String> sqls = sqlRecord.get();
        if (sqls == null) {
            sqls = new ArrayList<>();
        }
        sqls.add(sql);
        sqlRecord.set(sqls);
    }

}
