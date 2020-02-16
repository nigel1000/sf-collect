package com.common.collect.model.taskrecord;

import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.spring.SpringContextUtil;
import com.common.collect.model.taskrecord.infrastructure.TaskRecord;
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
public abstract class AbstractTaskProcess {

    public abstract IMetaConfig metaConfig();

    public List<TaskRecord> taskRecords(long startId) {
        IMetaConfig metaConfig = metaConfig();
        if (metaConfig == null) {
            return new ArrayList<>();
        }
        return SpringContextUtil.getBean(TaskRecordManager.class).loadNeedTaskRecord(metaConfig, startId);
    }

    public void handleTask() {
        long startId = 0;
        while (true) {
            List<TaskRecord> taskRecords = taskRecords(startId);
            if (EmptyUtil.isEmpty(taskRecords)) {
                break;
            }
            for (TaskRecord taskRecord : taskRecords) {
                if (taskRecord == null) {
                    continue;
                }
                if(log.isDebugEnabled()) {
                    log.debug("重试记录 {}", taskRecord);
                }
                startId = taskRecord.getId();
                try {
                    if (bizExecute(taskRecord)) {
                        SpringContextUtil.getBean(TaskRecordManager.class).success(taskRecord.getId(), metaConfig());
                    } else {
                        if (needNotifyAlert(taskRecord)) {
                            notifyAlert(taskRecord);
                        }
                        SpringContextUtil.getBean(TaskRecordManager.class).fail(taskRecord.getId(), metaConfig());
                    }
                } catch (Exception ex) {
                    if (needNotifyAlert(taskRecord)) {
                        notifyAlert(taskRecord);
                    }
                    log.error("重试记录 id:{} 失败异常。", taskRecord.getId(), ex);
                    SpringContextUtil.getBean(TaskRecordManager.class).failExp(taskRecord.getId(), ex, metaConfig());
                }
            }
        }
    }

    public boolean needNotifyAlert(@NonNull TaskRecord taskRecord) {
        return taskRecord.getTryTimes().equals(taskRecord.getMaxTryTimes());
    }

    // 最后一次尝试还是失败的情况下进行回调这个函数，选择实现
    public void notifyAlert(@NonNull TaskRecord taskRecord) {
        if (taskRecord.getAlertType() != null) {
            log.info("alterType:{},alertTarget:{}", taskRecord.getAlertType(), taskRecord.getAlertTarget());
            // String alertMsg = "taskRecord 执行失败，id: " + taskRecord.getId() + " .";
            // AlertUtil.alertWithTraceId(alertMsg, AlertUtil.Alert.valueOf(taskRecord.getAlertType()), taskRecord.getAlertTarget());
        }
    }

    public abstract boolean bizExecute(@NonNull TaskRecord taskRecord) throws Exception;

}
