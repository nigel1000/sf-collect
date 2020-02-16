package com.common.collect.model.flowlog.infrastructure;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hznijianfeng on 2020/02/15.
 */

public interface FlowLogMapper {

    Integer create(@Param("item") FlowLog item);

    Integer creates(@Param("items") List<FlowLog> items);

    Integer delete(@Param("id") Long id);

    Integer deletes(List<Long> ids);

    FlowLog load(@Param("id") Long id);

    List<FlowLog> loads(List<Long> ids);

    Integer update(@Param("item") FlowLog item);

}