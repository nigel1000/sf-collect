package com.common.collect.container.arrange.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.container.BeanUtil;
import com.common.collect.container.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
public class ExecuteParam {

    // 功能 key
    private String bizKey;
    private List<String> bizKeyRoute = new ArrayList<>();
    private String functionKey;
    private FunctionMethodTypeEnum functionMethodTypeEnum;
    // 执行类
    @JSONField(serialize = false)
    private Object target;
    // 执行方法
    @JSONField(serialize = false)
    private Method method;
    // 方法入参
    @JSONField(serialize = false)
    private Class<?>[] paramTypes;
    // 返回类型
    @JSONField(serialize = false)
    private Class<?> returnType;
    // 是否保存输入
    private Boolean functionInKeep;
    // 是否保存返回
    private Boolean functionOutKeep;
    // 方法参数 输入
    private FunctionMethodOutFromEnum functionMethodOutFromEnum;
    private Map<String, String> inOutMap = new LinkedHashMap<>();

    public static ExecuteParam gen(FunctionParam functionParam) {
        ExecuteParam executeParam = new ExecuteParam();
        executeParam.setFunctionKey(functionParam.getFunctionKey());
        executeParam.setFunctionMethodTypeEnum(FunctionMethodTypeEnum.valueOf(functionParam.getFunctionMethodType()));
        executeParam.setFunctionMethodOutFromEnum(FunctionMethodOutFromEnum.valueOf(functionParam.getFunctionMethodOutFrom()));
        executeParam.setTarget(functionParam.getFunctionClazz());
        Method method = functionParam.getMethod();
        executeParam.setMethod(method);
        executeParam.setParamTypes(method.getParameterTypes());
        executeParam.setReturnType(method.getReturnType());
        executeParam.setFunctionInKeep(functionParam.getFunctionInKeep());
        executeParam.setFunctionOutKeep(functionParam.getFunctionOutKeep());
        return executeParam;
    }

    public void putInOutputMap(String out, String in) {
        inOutMap.put(out, in);
    }

    public static List<ExecuteParam> copy(List<ExecuteParam> from) {
        if (EmptyUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        List<ExecuteParam> ret = new ArrayList<>();
        for (ExecuteParam executeParam : from) {
            ExecuteParam to = BeanUtil.genBean(executeParam, ExecuteParam.class);
            to.setBizKeyRoute(Lists.newArrayList(executeParam.getBizKeyRoute()));
            Map<String, String> inOutMap = new LinkedHashMap<>();
            inOutMap.putAll(executeParam.getInOutMap());
            to.setInOutMap(inOutMap);
            ret.add(to);
        }
        return ret;
    }

}
