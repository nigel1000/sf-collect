package com.common.collect.model.retry;

import com.common.collect.model.mapper.RetryRecordMapper;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.ExceptionUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/12.
 */

@Component("retryRecordManager")
public class RetryRecordManager {

    @Autowired(required = false)
    private RetryRecordMapper retryRecordMapper;

    public boolean record(@NonNull IMetaConfig metaConfig, @NonNull RetryRecord retryRecord) {
        return retryRecordMapper.create(retryRecord.validAdd(), metaConfig) == 1;
    }

    public boolean record(@NonNull IMetaConfig metaConfig,
                          String bizId,
                          String body,
                          Integer maxTryTimes,
                          Exception ex) {
        RetryRecord retryRecord = RetryRecord.of(metaConfig);
        retryRecord.setBizId(bizId);
        retryRecord.setBody(body);
        retryRecord.setMaxTryTimes(maxTryTimes);
        if (metaConfig.getAlertType() != null && EmptyUtil.isNotEmpty(metaConfig.getAlertTarget())) {
            retryRecord.setAlertType(metaConfig.getAlertType());
            retryRecord.setAlertTarget(metaConfig.getAlertTarget());
        }
        retryRecord.setFirstErrorMessage(ExceptionUtil.getStackTraceAsString(ex));
        return this.record(metaConfig, retryRecord);
    }

    public List<RetryRecord> loadNeedRetryRecord(IMetaConfig metaConfig, long startId) {
        return retryRecordMapper.loadNeedRetryRecord(metaConfig,startId);
    }

    public boolean fail(Long id, IMetaConfig metaConfig) {
        return retryRecordMapper.fail(id, metaConfig) == 1;
    }

    public boolean failExp(Long id, Exception ex, IMetaConfig metaConfig) {
        return retryRecordMapper.failExp(id, ExceptionUtil.getStackTraceAsString(ex), metaConfig) == 1;
    }

    public boolean success(Long id, IMetaConfig metaConfig) {
        return retryRecordMapper.success(id, metaConfig) == 1;
    }
}
