package com.common.collect.container.mybatis;

import com.common.collect.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Statement;
import java.util.*;

/**
 * Created by nijianfeng on 2018/10/28.
 */
// MyBatis插件机制非常有用，用得好可以解决很多问题，不只是这里的打印SQL语句以及记录SQL语句执行时间，分页、分表都可以通过插件来实现。
// Executor（update、query、flushStatements、commint、rollback、getTransaction、close、isClosed）
// ParameterHandler（getParameterObject、setParameters）
// ResultSetHandler（handleResultSets、handleOutputParameters）
// StatementHandler（prepare、parameterize、batch、update、query）
// 只有理解这四个接口及相关方法是干什么的，才能写出好的拦截器，开发出符合预期的功能。

// 有四个接口可以拦截，为什么使用StatementHandler去拦截？
// 根据名字来看ParameterHandler和ResultSetHandler，前者处理参数，后者处理结果是不可能使用的，
// 剩下的就是Executor和StatementHandler了。
// 拦截StatementHandler的原因是而不是用Executor的原因是：
// Executor的update与query方法可能用到MyBatis的一二级缓存从而导致统计的并不是真正的SQL执行时间
// StatementHandler的update与query方法无论如何都会统计到PreparedStatement的execute方法执行时间，尽管也有一定误差（误差主要来自将处理结果的时间也算上），但是相差不大
///////////////////////////////////////////////////////////////////////////
//<configuration>
//<settings>
//  <setting name="lazyLoadingEnabled" value="false"/>
//  <setting name="callSettersOnNulls" value="true"/>
//  <setting name="safeRowBoundsEnabled" value="false"/>
//  <!--设置启用数据库字段下划线映射到java对象的驼峰式命名属性，默认为false-->
//  <setting name="mapUnderscoreToCamelCase" value="true"/>
//</settings>
//<plugins>
//  <plugin interceptor="sf.house.mybatis.dao.plugins.SqlPrintInterceptor">
//      <property name="tableNameList" value="test"></property>
//  </plugin>
//</plugins>
//
//</configuration>
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
@Slf4j
public class SqlPrintInterceptor implements Interceptor {

    private List<String> tableNames = new ArrayList<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long sqlCost = System.currentTimeMillis() - startTime;

            MetaObject statementHandler = SystemMetaObject.forObject(invocation.getTarget());
            MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");
            BoundSql boundSql = (BoundSql) statementHandler.getValue("delegate.boundSql");
            boolean needShowSql = false;
            if (!CollectionUtils.isEmpty(tableNames)) {
                String sql = boundSql.getSql();
                for (String tableName : tableNames) {
                    if (sql.contains(tableName)) {
                        needShowSql = true;
                        break;
                    }
                }
            }
            if (needShowSql) {
                String sql = showSql(mappedStatement, boundSql);
                log.debug("SQL 执行耗时[{}ms],sqlId:[{}],sql:[{}]", sqlCost, mappedStatement.getId(), sql);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    // setProperties方法，可以将一些配置属性配置在<plugin></plugin>的子标签<property />中，
    // 所有的配置属性会在形参Properties中，setProperties方法可以拿到配置的属性进行需要的处理。
    @Override
    public void setProperties(Properties properties) {
        String tableName = properties.getProperty("tableNameList");
        if (StringUtils.isEmpty(tableName)) {
            return;
        }
        tableNames = Arrays.asList(tableName.trim().split(","));
    }

    private String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            value = "'" + DateUtil.format((Date) obj, "yyyy-MM-dd HH:mm:ss") + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    private String showSql(MappedStatement mappedStatement, BoundSql boundSql) {
        Configuration configuration = mappedStatement.getConfiguration();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    Object propertyValue;
                    if (metaObject.hasGetter(propertyName)) {
                        propertyValue = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(propertyValue));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        propertyValue = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(propertyValue));
                    }
                }
            }
        }
        return sql;
    }

}

