package com.common.collect.model.flowlog;

import com.common.collect.model.flowlog.infrastructure.FlowLog;
import com.common.collect.model.flowlog.infrastructure.FlowLogExt;
import com.common.collect.model.flowlog.infrastructure.FlowLogMapperExt;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2018/10/28.
 */

@Component
public class FlowLogManager {

    @Autowired(required = false)
    private FlowLogMapperExt flowLogMapperExt;

    public boolean record(@NonNull IMetaConfig metaConfig,
                          String bizId,
                          String updateValue,
                          String operatorId,
                          String operatorName) {
        FlowLog flowLog = FlowLogExt.of(metaConfig);
        flowLog.setBizId(bizId);
        flowLog.setUpdateValue(updateValue);
        flowLog.setOperatorId(operatorId);
        flowLog.setOperatorName(operatorName);
        return this.record(metaConfig, flowLog);
    }

    public boolean record(@NonNull IMetaConfig metaConfig, @NonNull FlowLog flowLog) {
        FlowLogExt.validAdd(flowLog);
        return flowLogMapperExt.record(metaConfig, flowLog) == 1;
    }

}
