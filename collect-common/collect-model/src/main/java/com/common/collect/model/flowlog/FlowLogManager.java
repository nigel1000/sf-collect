package com.common.collect.model.flowlog;

import com.common.collect.model.mapper.FlowLogMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2018/10/28.
 */

@Component
public class FlowLogManager {

    @Autowired(required = false)
    private FlowLogMapper flowLogMapper;

    public boolean record(@NonNull IMetaConfig metaConfig,
                          String bizId,
                          String updateValue,
                          String operatorId,
                          String operatorName) {
        FlowLog flowLog = FlowLog.of(metaConfig);
        flowLog.setBizId(bizId);
        flowLog.setUpdateValue(updateValue);
        flowLog.setOperatorId(operatorId);
        flowLog.setOperatorName(operatorName);
        return this.record(metaConfig, flowLog);
    }

    public boolean record(@NonNull IMetaConfig metaConfig, @NonNull FlowLog flowLog) {
        return flowLogMapper.record(metaConfig, flowLog.validAdd()) == 1;
    }

}
