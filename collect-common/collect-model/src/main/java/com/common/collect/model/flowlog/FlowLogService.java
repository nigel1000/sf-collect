package com.common.collect.model.flowlog;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.model.flowlog.mapper.FlowLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2018/10/28.
 */

@Component("flowLogService")
public class FlowLogService {

    @Autowired(required = false)
    private FlowLogMapper flowLogMapper;

    public Integer create(FlowLog flowLog, IMetaConfig metaConfig) {
        if (flowLog == null || metaConfig == null ||
                metaConfig.getBizType() == null) {
            throw UnifiedException.gen("流程日志参数不合法");
        }
        flowLog.setBizType(metaConfig.getBizType());
        flowLog.setBizTypeName(metaConfig.getBizName());
        return flowLogMapper.create(flowLog);
    }

    public Integer record(FlowLog flowLog, IMetaConfig metaConfig) {
        if (flowLog == null || metaConfig == null ||
                metaConfig.getBizType() == null) {
            throw UnifiedException.gen("流程日志参数不合法");
        }
        flowLog.setBizType(metaConfig.getBizType());
        flowLog.setBizTypeName(metaConfig.getBizName());
        return flowLogMapper.record(flowLog, metaConfig);
    }

}
