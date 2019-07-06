package com.common.collect.container.arrange.param;

import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
public class ExecuteParam {

    // 功能 key
    private String bizKey;
    private String functionKey;
    private FunctionMethodTypeEnum functionMethodTypeEnum;
    // 执行类
    private Object target;
    // 执行方法
    private Method method;
    // 方法入参
    private Class<?>[] paramTypes;
    // 返回类型
    private Class<?> returnType;
    // 是否保存输入
    private Boolean functionInKeep;
    // 是否保存返回
    private Boolean functionOutKeep;
    // 方法参数 输入
    private Map<String, String> inOutMap = new LinkedHashMap<>();

    public static ExecuteParam gen(FunctionParam functionParam) {
        ExecuteParam executeParam = new ExecuteParam();
        executeParam.setFunctionKey(functionParam.getFunctionKey());
        executeParam.setFunctionMethodTypeEnum(FunctionMethodTypeEnum.valueOf(functionParam.getFunctionMethodType()));
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


}
