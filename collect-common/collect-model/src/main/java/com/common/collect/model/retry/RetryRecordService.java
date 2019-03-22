package com.common.collect.model.retry;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/12.
 */

public interface RetryRecordService {

    Integer record(RetryRecord retryMsg, IMetaConfig metaConfig);

    List<RetryRecord> loadNeedRetryRecord(IMetaConfig metaConfig);

    Integer fail(Long id, IMetaConfig metaConfig);

    Integer failExp(Long id, Exception ex, IMetaConfig metaConfig);

    Integer success(Long id, IMetaConfig metaConfig);

}
