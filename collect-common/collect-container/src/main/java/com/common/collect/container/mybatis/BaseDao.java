package com.common.collect.container.mybatis;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.*;

/**
 * Created by nijianfeng on 2018/8/18.
 */

public abstract class BaseDao<T> extends SqlSessionDaoSupport {

    private static final String CREATE = "create";
    private static final String CREATES = "creates";
    private static final String DELETE = "delete";
    private static final String DELETES = "deletes";
    private static final String LOAD = "load";
    private static final String LOADS = "loads";
    private static final String UPDATE = "update";

    @Setter
    @Getter
    private String nameSpace;

    public BaseDao() {
        this.nameSpace = this.getClass().getName();
    }

    public abstract void init(SqlSessionFactory sqlSessionFactory);

    public Integer create(T t) {
        Map<String, T> map = new HashMap<>();
        map.put("item", t);
        return this.getSqlSession().insert(this.sqlId(CREATE), map);
    }

    public Integer creates(List<T> ts) {
        if (ts == null || ts.isEmpty()) {
            return 0;
        }
        return this.getSqlSession().insert(this.sqlId(CREATES), ts);
    }

    public Integer creates(@NonNull T... ts) {
        return creates(Arrays.asList(ts));
    }

    public Integer delete(@NonNull Long id) {
        return this.getSqlSession().delete(this.sqlId(DELETE), id);
    }

    public Integer deletes(@NonNull List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return this.getSqlSession().delete(this.sqlId(DELETES), ids);
    }

    public Integer deletes(@NonNull Long... ids) {
        return deletes(Arrays.asList(ids));
    }

    public T load(@NonNull Long id) {
        return this.getSqlSession().selectOne(this.sqlId(LOAD), id);
    }

    public List<T> loads(@NonNull List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return this.getSqlSession().selectList(this.sqlId(LOADS), ids);
    }

    public List<T> loads(@NonNull Long... ids) {
        return loads(Arrays.asList(ids));
    }

    public Integer update(@NonNull T t) {
        Map<String, T> map = new HashMap<>();
        map.put("item", t);
        return this.getSqlSession().update(this.sqlId(UPDATE), map);
    }

    private String sqlId(@NonNull String id) {
        return this.nameSpace + "." + id;
    }

}
