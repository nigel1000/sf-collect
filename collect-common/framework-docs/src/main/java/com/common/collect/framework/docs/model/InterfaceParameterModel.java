package com.common.collect.framework.docs.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/4/8.
 */
@Data
public class InterfaceParameterModel {
    //  该接口的请求参数
    private List<ParameterModel> inputs = new ArrayList<>();
    // 该接口的响应参数
    private List<ParameterModel> outputs = new ArrayList<>();

    public InterfaceParameterModel addInput(ParameterModel parameter) {
        inputs.add(parameter);
        return this;
    }

    public InterfaceParameterModel addInput(List<ParameterModel> parameters) {
        inputs.addAll(parameters);
        return this;
    }

    public InterfaceParameterModel addOutput(ParameterModel parameter) {
        outputs.add(parameter);
        return this;
    }

    public InterfaceParameterModel addOutput(List<ParameterModel> parameters) {
        outputs.addAll(parameters);
        return this;
    }
}
