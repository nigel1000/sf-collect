package com.common.collect.container.arrange.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BizDefineArrangeModel {

    private String type;
    @JSONField(deserialize = false, serialize = false)
    private TypeEnum typeEnum;
    private String name;
    private String inputType;
    private InputTypeEnum inputTypeEnum;
    private List<String> inputMappings;
    private List<String> inputExcludes;

    public void validSelf(BizDefineModel bizDefineModel) {
        try {
            this.setTypeEnum(TypeEnum.valueOf(type));
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("arrange 不合法，{}", this.getType()), ex);
        }
        if (EmptyUtil.isEmpty(inputType)) {
            inputType = InputTypeEnum.auto.name();
        }
        try {
            this.setInputTypeEnum(InputTypeEnum.valueOf(inputType));
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("arrange 不合法，{}", this.getType()), ex);
        }
        if (EmptyUtil.isEmpty(inputMappings)) {
            inputMappings = new ArrayList<>();
        }
        if (inputTypeEnum.equals(InputTypeEnum.assign)) {
            if (this.getTypeEnum().equals(TypeEnum.biz)) {
                if (EmptyUtil.isEmpty(this.getInputMappings())) {
                    throw UnifiedException.gen(StringUtil.format("type为biz,bizKey:{},name:{} 的 input 不能为空", bizDefineModel.getBizKey(),
                            this.getName()));
                }
            }
        }
        if (EmptyUtil.isEmpty(inputExcludes)) {
            inputExcludes = new ArrayList<>();
        }
    }

    public enum TypeEnum {

        function,
        biz,

        ;

    }

    public enum InputTypeEnum {

        auto,
        assign,

        ;

    }


}

