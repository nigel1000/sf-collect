package com.common.collect.container.arrange.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.util.List;

/**
 * Created by nijianfeng on 2019/7/6.
 */
@Data
public class BizDefineModel {

    private String bizKey;
    private Boolean saveModel;
    private List<BizDefineArrangeModel> arranges;

    public void validSelf() {
        if (EmptyUtil.isEmpty(bizKey)) {
            throw UnifiedException.gen(" bizKey 不能为空");
        }
        if (EmptyUtil.isEmpty(arranges)) {
            throw UnifiedException.gen(" arranges 不能为空");
        }
        if (saveModel == null) {
            saveModel = false;
        }

        for (BizDefineArrangeModel arrange : arranges) {
            arrange.validSelf(this);
        }
    }
}


