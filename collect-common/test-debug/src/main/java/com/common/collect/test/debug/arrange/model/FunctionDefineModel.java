package com.common.collect.test.debug.arrange.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.test.debug.arrange.enums.FunctionClassTypeEnum;
import com.common.collect.test.debug.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.test.debug.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.test.debug.arrange.strategy.FunctionClazzFactory;
import com.common.collect.test.debug.arrange.strategy.FunctionMethodFactory;
import lombok.Data;

import java.lang.reflect.Field;
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
            functionOutKeep = false;
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

        Class<?> paramType = null;
        if (EmptyUtil.isNotEmpty(functionMethodInClazz)) {
            paramType = ClassUtil.getClass(functionMethodInClazz);
            if (EmptyUtil.isEmpty(functionMethodInFields)) {
                functionMethodInFields = new ArrayList<>();
                for (Field declaredField : paramType.getDeclaredFields()) {
                    functionMethodInFields.add(declaredField.getName());
                }
            }
        } else {
            if (EmptyUtil.isNotEmpty(functionMethodInFields)) {
                throw UnifiedException.gen(StringUtil.format("没有定义 functionMethodInClazz 时不能配置 functionMethodInFields"));
            }
            if (functionMethodOutFromEnum.equals(FunctionMethodOutFromEnum.input)) {
                throw UnifiedException.gen(StringUtil.format("没有定义 functionMethodInClazz 时不能配置 functionMethodOutFrom 为 input"));
            }
            functionMethodInFields = new ArrayList<>();
        }

        functionClazz = FunctionClazzFactory.getInstance(this);
        functionMethod = FunctionMethodFactory.getInstance(this);
        Class<?> returnType = functionMethod.getReturnType();
        if (EmptyUtil.isEmpty(functionMethodOutFields)) {
            functionMethodOutFields = new ArrayList<>();
            for (Field declaredField : returnType.getDeclaredFields()) {
                functionMethodOutFields.add(declaredField.getName());
            }
        }

        for (String functionMethodInField : functionMethodInFields) {
            ClassUtil.getDeclaredField(paramType, functionMethodInField);
        }

        for (String functionMethodOutField : functionMethodOutFields) {
            if (functionMethodOutFromEnum.equals(FunctionMethodOutFromEnum.output)) {
                ClassUtil.getDeclaredField(returnType, functionMethodOutField);
            } else if (functionMethodOutFromEnum.equals(FunctionMethodOutFromEnum.input)) {
                ClassUtil.getDeclaredField(paramType, functionMethodOutField);
            }
        }

    }

}


