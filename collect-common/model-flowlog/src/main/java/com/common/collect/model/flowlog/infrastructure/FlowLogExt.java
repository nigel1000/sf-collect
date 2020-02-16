package com.common.collect.model.flowlog.infrastructure;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.model.flowlog.IMetaConfig;
import lombok.NonNull;

/**
 * Created by nijianfeng on 2020/2/15.
 */
public class FlowLogExt {

    public static FlowLog of(@NonNull IMetaConfig metaConfig) {
        FlowLog flowLog = new FlowLog();
        flowLog.setBizType(metaConfig.getBizType());
        return flowLog;
    }

    public static void validAdd(FlowLog domain) {
        if (domain == null) {
            return;
        }
        if (EmptyUtil.isBlank(domain.getBizType())) {
            throw UnifiedException.gen("bizType 不合理");
        }
        return;
    }

}
