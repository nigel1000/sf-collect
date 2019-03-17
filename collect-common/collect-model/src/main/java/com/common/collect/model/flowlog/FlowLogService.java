package com.common.collect.model.flowlog;

/**
 * Created by nijianfeng on 2018/10/28.
 */
public interface FlowLogService {

    Integer record(IMetaConfig metaConfig, FlowLog flowLog);

}