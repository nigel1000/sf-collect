package com.common.collect.model.retry;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Data
@Slf4j
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
                    failExecute(retryRecord);
                    retryRecordService.fail(retryRecord.getId(), metaConfig);
                }
            } catch (Exception ex) {
                failExecute(retryRecord);
                log.error("重试记录 id:{} 失败异常。", retryRecord.getId(), ex);
                retryRecordService.failExp(retryRecord.getId(), ex, metaConfig);
            }
        }
    }

    public void failExecute(RetryRecord retryRecord) {}

    public abstract boolean bizExecute(RetryRecord retryRecord) throws Exception;

}
