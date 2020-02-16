package com.common.collect.model.taskrecord.infrastructure;

import com.common.collect.lib.api.enums.YesNoEnum;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.model.taskrecord.IMetaConfig;
import lombok.NonNull;

public class TaskRecordExt {

    public static TaskRecord of(@NonNull IMetaConfig metaConfig) {
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setBizType(metaConfig.getBizType());
        return taskRecord;
    }

    public static void validAdd(TaskRecord domain) {
        if (domain == null) {
            return;
        }
        if (EmptyUtil.isBlank(domain.getBizType())) {
            throw UnifiedException.gen("bizType 不合理");
        }
        if (domain.getTryTimes() == null) {
            domain.setTryTimes(0);
        }
        if (domain.getMaxTryTimes() == null) {
            domain.setMaxTryTimes(3);
        }
        if (domain.getState() == null) {
            domain.setState(YesNoEnum.NO.getCode());
        }
        if (domain.getBizId() == null) {
            domain.setBizId("");
        }
    }
}