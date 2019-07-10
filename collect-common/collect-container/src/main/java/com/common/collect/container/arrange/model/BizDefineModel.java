package com.common.collect.container.arrange.model;

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
    private List<BizDefineArrangeModel> arranges;

    public void validSelf() {
        if (EmptyUtil.isEmpty(bizKey)) {
            throw UnifiedException.gen(" bizKey 不能为空");
        }
        if (EmptyUtil.isEmpty(arranges)) {
            throw UnifiedException.gen(" arranges 不能为空");
        }

        for (BizDefineArrangeModel arrange : arranges) {
            arrange.validSelf(this);
        }
    }
}


