package com.common.collect.model.taskrecord;

import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.ExceptionUtil;
import com.common.collect.model.taskrecord.infrastructure.TaskRecord;
import com.common.collect.model.taskrecord.infrastructure.TaskRecordExt;
import com.common.collect.model.taskrecord.infrastructure.TaskRecordMapperExt;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/12.
 */

@Component
public class TaskRecordManager {

    @Autowired(required = false)
    private TaskRecordMapperExt taskRecordMapperExt;

    public boolean record(@NonNull IMetaConfig metaConfig, @NonNull TaskRecord taskRecord) {
        TaskRecordExt.validAdd(taskRecord);
        return taskRecordMapperExt.create(taskRecord, metaConfig) == 1;
    }

    public boolean record(@NonNull IMetaConfig metaConfig,
                          String bizId,
                          String body,
                          Integer maxTryTimes,
                          Exception ex) {
        TaskRecord taskRecord = TaskRecordExt.of(metaConfig);
        taskRecord.setBizId(bizId);
        taskRecord.setBody(body);
        taskRecord.setMaxTryTimes(maxTryTimes);
        if (metaConfig.getAlertType() != null && EmptyUtil.isNotEmpty(metaConfig.getAlertTarget())) {
            taskRecord.setAlertType(metaConfig.getAlertType());
            taskRecord.setAlertTarget(metaConfig.getAlertTarget());
        }
        taskRecord.setFirstErrorMessage(ExceptionUtil.getStackTraceAsString(ex));
        return this.record(metaConfig, taskRecord);
    }

    public List<TaskRecord> loadNeedTaskRecord(IMetaConfig metaConfig, long startId) {
        return taskRecordMapperExt.loadNeedTaskRecord(metaConfig, startId);
    }

    public boolean fail(Long id, IMetaConfig metaConfig) {
        return taskRecordMapperExt.fail(id, metaConfig) == 1;
    }

    public boolean failExp(Long id, Exception ex, IMetaConfig metaConfig) {
        return taskRecordMapperExt.failExp(id, ExceptionUtil.getStackTraceAsString(ex), metaConfig) == 1;
    }

    public boolean success(Long id, IMetaConfig metaConfig) {
        return taskRecordMapperExt.success(id, metaConfig) == 1;
    }
}
