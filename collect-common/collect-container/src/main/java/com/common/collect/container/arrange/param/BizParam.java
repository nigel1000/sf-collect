package com.common.collect.container.arrange.param;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.arrange.enums.ArrangeTypeEnum;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.util.List;

/**
 * Created by nijianfeng on 2019/7/6.
 */
@Data
public class BizParam {

    private String bizKey;
    private List<ArrangeParam> arranges;

    private List<ExecuteParam> executeChains;

    public void validSelf() {
        if (EmptyUtil.isEmpty(bizKey)) {
            throw UnifiedException.gen("BizContext bizKey 不能为空");
        }
        if (EmptyUtil.isEmpty(arranges)) {
            throw UnifiedException.gen("BizContext arranges 不能为空");
        }
        int size = arranges.size();
        for (int i = 0; i < size; i++) {
            ArrangeParam context = arranges.get(i);
            try {
                ArrangeTypeEnum.valueOf(context.getType());
            } catch (Exception ex) {
                throw UnifiedException.gen("BizContext typeEnum 不合法", ex);
            }
            if (context.getType().equals(ArrangeTypeEnum.biz.name())) {
                if (EmptyUtil.isEmpty(context.getInput())) {
                    throw UnifiedException.gen("biz: " + bizKey + " 里的 type为biz，name:" + context.getName() + "的 input 不能为空");
                }
            }
        }
    }
}


