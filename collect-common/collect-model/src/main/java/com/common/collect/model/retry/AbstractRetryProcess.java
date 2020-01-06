package com.common.collect.model.retry;

import com.common.collect.container.SpringContextUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Data
@Slf4j
public abstract class AbstractRetryProcess {

    public abstract IMetaConfig metaConfig();

    public List<RetryRecord> retryRecords(long startId) {
        IMetaConfig metaConfig = metaConfig();
        if (metaConfig == null) {
            return new ArrayList<>();
        }
        return SpringContextUtil.getBean(RetryRecordManager.class).loadNeedRetryRecord(metaConfig, startId);
    }

    public void handleRetry() {
        long startId = 0;
        while (true) {
            List<RetryRecord> retryRecords = retryRecords(startId);
            if (EmptyUtil.isEmpty(retryRecords)) {
                break;
            }
            for (RetryRecord retryRecord : retryRecords) {
                if (retryRecord == null) {
                    continue;
                }
                if(log.isDebugEnabled()) {
                    log.debug("重试记录 {}", retryRecord);
                }
                startId = retryRecord.getId();
                try {
                    if (bizExecute(retryRecord)) {
                        SpringContextUtil.getBean(RetryRecordManager.class).success(retryRecord.getId(), metaConfig());
                    } else {
                        if (needNotifyAlert(retryRecord)) {
                            notifyAlert(retryRecord);
                        }
                        SpringContextUtil.getBean(RetryRecordManager.class).fail(retryRecord.getId(), metaConfig());
                    }
                } catch (Exception ex) {
                    if (needNotifyAlert(retryRecord)) {
                        notifyAlert(retryRecord);
                    }
                    log.error("重试记录 id:{} 失败异常。", retryRecord.getId(), ex);
                    SpringContextUtil.getBean(RetryRecordManager.class).failExp(retryRecord.getId(), ex, metaConfig());
                }
            }
        }
    }

    public boolean needNotifyAlert(@NonNull RetryRecord retryRecord) {
        return retryRecord.getTryTimes().equals(retryRecord.getMaxTryTimes());
    }

    // 最后一次尝试还是失败的情况下进行回调这个函数，选择实现
    public void notifyAlert(@NonNull RetryRecord retryRecord) {
        if (retryRecord.getAlertType() != null) {
            log.info("alterType:{},alertTarget:{}", retryRecord.getAlertType(), retryRecord.getAlertTarget());
            // String alertMsg = "retryRecord 执行失败，id: " + retryRecord.getId() + " .";
            // AlertUtil.alertWithTraceId(alertMsg, AlertUtil.Alert.valueOf(taskRecord.getAlertType()), taskRecord.getAlertTarget());
        }
    }

    public abstract boolean bizExecute(@NonNull RetryRecord retryRecord) throws Exception;

}
