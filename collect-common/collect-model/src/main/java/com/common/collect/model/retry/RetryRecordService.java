package com.common.collect.model.retry;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/12.
 */

public interface RetryRecordService {

    Integer record(RetryRecord retryMsg, IMetaConfig retryMeta);

    List<RetryRecord> loadNeedRetryMsg(IMetaConfig retryMeta);

    Integer fail(Long id, IMetaConfig retryMeta);

    Integer failExp(Long id, Exception ex, IMetaConfig retryMeta);

    Integer success(Long id, IMetaConfig retryMeta);

}
