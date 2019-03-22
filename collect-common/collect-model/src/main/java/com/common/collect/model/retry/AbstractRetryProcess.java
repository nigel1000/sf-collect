package com.common.collect.model.retry;

import lombok.Data;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Data
public abstract class AbstractRetryProcess {

    private IMetaConfig metaConfig;
    private RetryRecordService retryRecordService;

    public abstract void init();

    public void handleRetry() {
        List<RetryRecord> retryRecords = retryRecordService.loadNeedRetryRecord(metaConfig);
        for (RetryRecord retryRecord : retryRecords) {
            try {
                if (bizExecute(retryRecord)) {
                    retryRecordService.success(retryRecord.getId(), metaConfig);
                } else {
                    retryRecordService.fail(retryRecord.getId(), metaConfig);
                }
            } catch (Exception ex) {
                retryRecordService.failExp(retryRecord.getId(), ex, metaConfig);
            }
        }
    }

    public abstract boolean bizExecute(RetryRecord retryRecord) throws Exception;


}
