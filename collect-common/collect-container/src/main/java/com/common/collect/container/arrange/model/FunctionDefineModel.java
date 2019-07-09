package com.common.collect.container.arrange.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.arrange.enums.FunctionClassTypeEnum;
import com.common.collect.container.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.container.arrange.strategy.FunctionClazzFactory;
import com.common.collect.container.arrange.strategy.FunctionMethodFactory;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/7/6.
 */
@Data
public class FunctionDefineModel {

    // 功能 key
    private String functionKey;
    // 是否保存输入
    private Boolean functionInKeep;
    // 是否保存返回
    private Boolean functionOutKeep;
    // 类实例获取方式  springByName|reflect
    @JSONField(deserialize = false, serialize = false)
    private Object functionClazz;
    private String functionClazzType;
    @JSONField(deserialize = false, serialize = false)
    private FunctionClassTypeEnum functionClassTypeEnum;
    private String functionClazzKey;
    // 功能 方法 入参少于等于 1
    @JSONField(deserialize = false, serialize = false)
    private Method functionMethod;
    private String functionMethodName;
    private String functionMethodType;
    @JSONField(deserialize = false, serialize = false)
    private FunctionMethodTypeEnum functionMethodTypeEnum;
    private String functionMethodInClazz;
    // 入参 关键属性 可为空 必须是 function_method_clazz 的 field
    private List<String> functionMethodInFields;
    // 导出来自 入参 input | 返回 output
    private String functionMethodOutFrom;
    @JSONField(deserialize = false, serialize = false)
    private FunctionMethodOutFromEnum functionMethodOutFromEnum;
    // 返回 关键属性 可为空 必须是 function_method_clazz 的 field，生成 json 作为 下一个 function_name 的 function_method_in_json
    private List<String> functionMethodOutFields;

    public void validSelf() {

        if (EmptyUtil.isEmpty(functionKey)) {
            throw UnifiedException.gen(" functionKey 不能为空");
        }
        if (functionInKeep == null) {
            functionInKeep = false;
        }
        if (functionOutKeep == null) {
            functionInKeep = false;
        }
        if (EmptyUtil.isEmpty(functionClazzType)) {
            functionClazzType = FunctionClassTypeEnum.springByName.name();
        }
        try {
            functionClassTypeEnum = FunctionClassTypeEnum.valueOf(functionClazzType);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("functionClazzType 不合法，{}", functionClazzType), ex);
        }
        if (EmptyUtil.isEmpty(functionClazzKey)) {
            throw UnifiedException.gen(" functionClazzKey 不能为空");
        }
        if (EmptyUtil.isEmpty(functionMethodName)) {
            throw UnifiedException.gen(" functionMethodName 不能为空");
        }
        if (EmptyUtil.isEmpty(functionMethodType)) {
            functionMethodType = FunctionMethodTypeEnum.inputLessEqualOne.name();
        }
        try {
            functionMethodTypeEnum = FunctionMethodTypeEnum.valueOf(functionMethodType);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("functionMethodType 不合法，{}", functionMethodType), ex);
        }
        if (EmptyUtil.isEmpty(functionMethodOutFrom)) {
            functionMethodOutFrom = FunctionMethodOutFromEnum.output.name();
        }
        try {
            functionMethodOutFromEnum = FunctionMethodOutFromEnum.valueOf(functionMethodOutFrom);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("functionMethodOutFrom 不合法，{}", functionMethodOutFrom), ex);
        }

        if (EmptyUtil.isNotEmpty(functionMethodInClazz)) {
            ClassUtil.getClass(functionMethodInClazz);
        }

        if (EmptyUtil.isEmpty(functionMethodInFields)) {
            functionMethodInFields = new ArrayList<>();
        }
        if (EmptyUtil.isEmpty(functionMethodOutFields)) {
            functionMethodOutFields = new ArrayList<>();
        }

        functionClazz = FunctionClazzFactory.getInstance(this);
        functionMethod = FunctionMethodFactory.getInstance(this);

    }

}


