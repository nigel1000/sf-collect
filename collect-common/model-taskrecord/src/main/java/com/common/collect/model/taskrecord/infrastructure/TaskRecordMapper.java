package com.common.collect.model.taskrecord.infrastructure;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hznijianfeng on 2020/02/15.
 */

public interface TaskRecordMapper {

    Integer create(@Param("item") TaskRecord item);

    Integer creates(@Param("items") List<TaskRecord> items);

    Integer delete(@Param("id") Long id);

    Integer deletes(List<Long> ids);

    TaskRecord load(@Param("id") Long id);

    List<TaskRecord> loads(List<Long> ids);

    Integer update(@Param("item") TaskRecord item);

}