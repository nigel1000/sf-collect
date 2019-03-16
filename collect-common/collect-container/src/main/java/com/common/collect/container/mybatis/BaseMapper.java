package com.common.collect.container.mybatis;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/03/14.
 */

public interface BaseMapper<T> {

    Integer create(@Param("item") T item);

    Integer creates(List<T> items);

    Integer delete(@Param("id") Long id);

    Integer deletes(List<Long> ids);

    T load(@Param("id") Long id);

    List<T> loads(List<Long> ids);

    Integer update(@Param("item") T item);

}