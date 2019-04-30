package com.common.collect.model.retry;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.model.retry.mapper.RetryRecordMapper;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/12.
 */

@Component("retryRecordService")
public class RetryRecordService {

    @Resource
    private RetryRecordMapper retryRecordMapper;

    public Integer record(RetryRecord retryRecord, IMetaConfig metaConfig) {
        if (retryRecord == null || metaConfig == null || metaConfig.getTableName() == null ||
                metaConfig.getBizType() == null || metaConfig.getMsgKey() == null || metaConfig.getMsgType() == null) {
            throw UnifiedException.gen("重试纪录参数不合法");
        }
        retryRecord.setBizType(metaConfig.getBizType());
        retryRecord.setMsgType(metaConfig.getMsgType());
        retryRecord.setMsgKey(metaConfig.getMsgKey());
        return retryRecordMapper.create(retryRecord, metaConfig);
    }

    public List<RetryRecord> loadNeedRetryRecord(IMetaConfig metaConfig) {
        return retryRecordMapper.loadNeedRetryRecord(metaConfig);
    }

    public List<RetryRecord> loadNeedRetryRecordByBizId(@NonNull String bizId, IMetaConfig metaConfig) {
        return retryRecordMapper.loadNeedRetryRecordByBizId(bizId, metaConfig);
    }

    public Integer fail(Long id, IMetaConfig metaConfig) {
        return retryRecordMapper.fail(id, metaConfig);
    }

    public Integer failExp(Long id, Exception ex, IMetaConfig metaConfig) {
        return retryRecordMapper.failExp(id, RetryRecord.subErrorMessage(ex), metaConfig);
    }

    public Integer success(Long id, IMetaConfig metaConfig) {
        return retryRecordMapper.success(id, metaConfig);
    }
}
