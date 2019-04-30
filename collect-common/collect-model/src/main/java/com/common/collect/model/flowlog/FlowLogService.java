package com.common.collect.model.flowlog;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.model.flowlog.mapper.FlowLogMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 2018/10/28.
 */

@Component("flowLogService")
public class FlowLogService {

    @Resource
    private FlowLogMapper flowLogMapper;

    public Integer record(IMetaConfig metaConfig, FlowLog flowLog) {
        if (flowLog == null || metaConfig == null ||
                metaConfig.getBizType() == null) {
            throw UnifiedException.gen("流程日志参数不合法");
        }
        flowLog.setBizType(metaConfig.getBizType());
        flowLog.setBizTypeName(metaConfig.getBizName());
        return flowLogMapper.create(flowLog);
    }
}
