package com.common.collect.debug.mybatis;


import com.common.collect.api.excps.UnifiedException;
import com.common.collect.debug.mybatis.generator.domain.db.Field;
import com.common.collect.debug.mybatis.generator.domain.db.Table;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Slf4j
public class DBUtil {

    public static String getJavaTypeBySqlType(String sqlType) {
        Map<String, String> typeMap = Maps.newHashMap();
        typeMap.put("datetime", "Date");
        typeMap.put("date", "Date");
        typeMap.put("timestamp", "Date");

        typeMap.put("varchar", "String");
        typeMap.put("char", "String");
        typeMap.put("mediumtext", "String");
        typeMap.put("text", "String");

        typeMap.put("tinyint", "Integer");
        typeMap.put("smallint", "Integer");
        typeMap.put("int", "Integer");


        typeMap.put("mediumint", "Long");
        typeMap.put("bigint", "Long");

        typeMap.put("double", "BigDecimal");
        typeMap.put("decimal", "BigDecimal");

        typeMap.put("bit", "Boolean");
        return typeMap.get(sqlType);
    }

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static Connection getConnection(GlobalParam globalParam) {
        try {
            String key = globalParam.getDbUrl();
            Connection connection = connectionMap.get(key);
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(globalParam.getDbUrl(), globalParam.getDbUser(), globalParam.getDbPwd());
                connectionMap.put(key, connection);
            }
            return connection;
        } catch (Exception e) {
            throw UnifiedException.gen("db 获取 Connection 失败", e);
        }
    }

    public static void releaseResource() {
        for (Connection value : connectionMap.values()) {
            close(value);
        }
    }

    private static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ex) {
                    throw UnifiedException.gen("db 关闭资源失败", ex);
                }
            }
        }
    }

    public static Map<String, Table> getTables(GlobalParam globalParam, List<String> tableNames) {
        if (EmptyUtil.isEmpty(tableNames )) {
            tableNames = getTableNamesBySchema(globalParam);
        }
        Map<String, Table> tableMap = Maps.newHashMap();
        for (String name : tableNames) {
            tableMap.putIfAbsent(name, getTable(globalParam, name));
        }
        return tableMap;
    }

    private static Table getTable(GlobalParam globalParam, String tableName) {
        log.info("db start query " + tableName);

        final String SELECT_FIELD = " column_name as field,";
        final String SELECT_TYPE = " data_type as type,";
        final String SELECT_MEMO = " column_comment as memo,";
        final String SELECT_NUMERIC_LENGTH = " numeric_precision as numericLength,";
        final String SELECT_NUMERIC_SCALE = " numeric_scale as numericScale, ";
        final String SELECT_IS_NULLABLE = " is_nullable as isNullable,";
        final String SELECT_IS_AUTO_INCREMENT =
                " CASE WHEN extra = 'auto_increment' THEN 'true' ELSE 'false' END as isAutoIncrement,";
        final String SELECT_IS_DEFAULT = " column_default as isDefault,";
        final String SELECT_CHARACTER_LENGTH = " character_maximum_length  AS characterLength ";
        final String SELECT_SCHEMA = "SELECT " + SELECT_FIELD + SELECT_TYPE + SELECT_MEMO
                + SELECT_NUMERIC_LENGTH + SELECT_NUMERIC_SCALE + SELECT_IS_NULLABLE + SELECT_IS_AUTO_INCREMENT
                + SELECT_IS_DEFAULT + SELECT_CHARACTER_LENGTH + " FROM Information_schema.columns "
                + "WHERE  table_schema ='" + globalParam.getDbSchema() + "' AND table_Name = ";
        String sql = SELECT_SCHEMA + "'" + tableName + "'";
        log.info("db execute sql: " + sql);
        try {
            PreparedStatement ps = getConnection(globalParam).prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Field> fields = Lists.newArrayList();
            while (rs.next()) {
                Field field = new Field();
                field.setField(rs.getString(1));
                field.setType(rs.getString(2));
                field.setMemo(rs.getString(3));
                field.setNumericLength(rs.getString(4));
                field.setNumericScale(rs.getString(5));
                field.setIsNullable(rs.getString(6));
                field.setIsAutoIncrement(Boolean.valueOf(rs.getString(7)));
                field.setIsDefault(rs.getString(8));
                field.setCharacterLength(rs.getString(9));
                fields.add(field);
                // 打印数据库某个表每列的返回数据
                log.info("db {} field:{}", tableName, field);
            }
            // 获取表描述
            ps = getConnection(globalParam)
                    .prepareStatement("SELECT table_comment FROM Information_schema.tables WHERE table_Name =" + "'" + tableName + "'");
            rs = ps.executeQuery();
            String tableComment = "无";
            while (rs.next()) {
                tableComment = rs.getString(1);
            }
            close(rs, ps);
            log.info("db end query " + tableName);
            Table table = new Table();
            table.setName(tableName);
            table.setComment(tableComment);
            table.setFields(fields);
            return table;
        } catch (Exception ex) {
            throw UnifiedException.gen("db 获取 schema:" + globalParam.getDbSchema() + ",tableName:" + tableName + " 数据失败", ex);
        }
    }

    private static List<String> getTableNamesBySchema(GlobalParam globalParam) {
        List<String> names = Lists.newArrayList();
        try {
            // 获取表名列表
            PreparedStatement ps = getConnection(globalParam).prepareStatement(
                    "SELECT table_name FROM Information_schema.tables WHERE table_schema = " + "'" + globalParam.getDbSchema() + "'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            close(rs, ps);
        } catch (Exception e) {
            throw UnifiedException.gen("获取 table name 失败", e);
        }
        return names;
    }

}
