package com.common.collect.test.debug.arrange.context;

import com.alibaba.fastjson.annotation.JSONField;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.spring.BeanUtil;
import com.common.collect.test.debug.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.test.debug.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.test.debug.arrange.model.FunctionDefineModel;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class BizFunctionChain {
    // 功能 key
    private String bizKey;
    private List<String> bizKeyRoute = new ArrayList<>();
    private String functionKey;
    @JSONField(deserialize = false, serialize = false)
    private FunctionMethodTypeEnum functionMethodTypeEnum;
    // 执行类
    @JSONField(deserialize = false, serialize = false)
    private Object target;
    // 执行方法
    @JSONField(deserialize = false, serialize = false)
    private Method method;
    // 方法入参
    @JSONField(deserialize = false, serialize = false)
    private Integer paramCount;
    @JSONField(deserialize = false, serialize = false)
    private Class<?>[] paramTypes;
    // 返回类型
    @JSONField(deserialize = false, serialize = false)
    private Class<?> returnType;
    // 是否保存输入
    @JSONField(deserialize = false, serialize = false)
    private Boolean functionInKeep;
    // 是否保存返回
    @JSONField(deserialize = false, serialize = false)
    private Boolean functionOutKeep;
    // 方法参数 输入
    private FunctionMethodOutFromEnum functionMethodOutFromEnum;
    private Map<String, String> inOutMap = new LinkedHashMap<>();
    private InputTypeEnum inputTypeEnum;

    public static BizFunctionChain gen(FunctionDefineModel functionDefineModel) {
        BizFunctionChain bizFunctionChain = new BizFunctionChain();
        bizFunctionChain.setFunctionKey(functionDefineModel.getFunctionKey());
        bizFunctionChain.setFunctionMethodTypeEnum(functionDefineModel.getFunctionMethodTypeEnum());
        bizFunctionChain.setFunctionMethodOutFromEnum(functionDefineModel.getFunctionMethodOutFromEnum());
        bizFunctionChain.setTarget(functionDefineModel.getFunctionClazz());
        Method method = functionDefineModel.getFunctionMethod();
        bizFunctionChain.setMethod(method);
        bizFunctionChain.setParamCount(method.getParameterCount());
        bizFunctionChain.setParamTypes(method.getParameterTypes());
        bizFunctionChain.setReturnType(method.getReturnType());
        bizFunctionChain.setFunctionInKeep(functionDefineModel.getFunctionInKeep());
        bizFunctionChain.setFunctionOutKeep(functionDefineModel.getFunctionOutKeep());
        return bizFunctionChain;
    }

    public void putInOutputMap(String out, String in) {
        inOutMap.put(out, in);
    }

    public void putIfAbsentInOutputMap(String out, String in) {
        inOutMap.putIfAbsent(out, in);
    }

    public static List<BizFunctionChain> copy(List<BizFunctionChain> from) {
        if (EmptyUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        List<BizFunctionChain> ret = new ArrayList<>();
        for (BizFunctionChain bizFunctionChain : from) {
            BizFunctionChain to = BeanUtil.genBean(bizFunctionChain, BizFunctionChain.class);
            to.setBizKeyRoute(new ArrayList<>(bizFunctionChain.getBizKeyRoute()));
            Map<String, String> inOutMap = new LinkedHashMap<>(bizFunctionChain.getInOutMap());
            to.setInOutMap(inOutMap);
            ret.add(to);
        }
        return ret;
    }

    public String bizKeyRoutePath() {
        return StringUtil.join(this.getBizKeyRoute(), "#") + "#" + this.getFunctionKey();
    }

    public enum InputTypeEnum {

        none,
        auto,
        assign,
        pass,;

    }

}