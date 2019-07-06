package com.common.collect.container.arrange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.YamlUtil;
import com.common.collect.container.arrange.constants.Constants;
import com.common.collect.container.arrange.enums.ArrangeTypeEnum;
import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.container.arrange.param.ArrangeParam;
import com.common.collect.container.arrange.param.BizParam;
import com.common.collect.container.arrange.param.ExecuteParam;
import com.common.collect.container.arrange.param.FunctionParam;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.ConvertUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.SplitUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
@Slf4j
public class ArrangeContext {

    private static Map<String, BizParam> bizParamMap = new LinkedHashMap<>();
    private static Map<String, FunctionParam> functionParamMap = new LinkedHashMap<>();

    private ArrangeContext() {
    }

    public static ArrangeRetContext runBiz(String bizKey, String paramJson) {
        BizParam bizContext = bizParamMap.get(bizKey);
        if (bizContext == null) {
            throw UnifiedException.gen("不存在此业务编排 " + bizKey);
        }
        ArrangeRetContext retContext = new ArrangeRetContext();
        retContext.setBizKey(bizContext.getBizKey());
        List<ExecuteParam> executeParams = bizContext.getExecuteChains();
        int size = executeParams.size();
        Object ret = null;
        for (int i = 0; i < size; i++) {
            ExecuteParam executeParam = executeParams.get(i);
            if (executeParam.getFunctionMethodTypeEnum().equals(FunctionMethodTypeEnum.inputLessEqualOne)) {
                Object arg = null;
                Class<?> paramType = executeParam.getParamTypes()[0];
                if (i == 0) {
                    if (paramJson != null) {
                        arg = JsonUtil.json2bean(paramJson, paramType);
                    }
                } else {
                    Map<String, Object> paramMap = new HashMap<>();
                    if (ret != null) {
                        Map<String, String> inOutMap = executeParam.getInOutMap();
                        for (Map.Entry<String, String> entry : inOutMap.entrySet()) {
                            String outField = entry.getKey();
                            String inField = entry.getValue();
                            paramMap.put(inField, ClassUtil.getFieldValue(ret, outField));
                        }
                    }
                    if (EmptyUtil.isNotEmpty(paramMap)) {
                        arg = JsonUtil.json2bean(JsonUtil.bean2json(paramMap), paramType);
                    }
                }
                ret = ClassUtil.invoke(executeParam.getTarget(), executeParam.getMethod(), arg);
                if (executeParam.getFunctionInKeep()) {
                    retContext.putInputMap(executeParam.getFunctionKey() + "-" + i, arg == null ? "null" : arg);
                }
                if (executeParam.getFunctionOutKeep()) {
                    retContext.putOutputMap(executeParam.getFunctionKey() + "-" + i, ret == null ? "null" : ret);
                }
            } else {
                throw UnifiedException.gen(executeParam.getTarget().getClass().getName() + "#" + executeParam.getMethod().getName() + " 入参只能是一个");
            }
        }
        return retContext;
    }

    public synchronized static void load(Object... obj) {
        Map<String, BizParam> bizParamMap = new LinkedHashMap<>();
        for (Object o : obj) {
            LinkedHashMap content = YamlUtil.parse(o);
            functionParamMap.putAll(initFunctions((LinkedHashMap) content.get(Constants.functions_define)));
            bizParamMap.putAll(initBiz((LinkedHashMap) content.get(Constants.biz_define)));
        }
        initExecuteChains(bizParamMap);
        ArrangeContext.bizParamMap.putAll(bizParamMap);
    }

    private static List<String> currentBizKeys = new ArrayList<>();

    private static void initExecuteChains(Map<String, BizParam> bizParamMap) {
        currentBizKeys.clear();
        for (Map.Entry<String, BizParam> entry : bizParamMap.entrySet()) {
            BizParam bizContext = entry.getValue();
            initExecuteChains(bizContext, bizParamMap);
        }
    }

    private static void initExecuteChains(BizParam bizParam, Map<String, BizParam> bizParamMap) {
        currentBizKeys.add(bizParam.getBizKey());
        int size = bizParam.getArranges().size();
        List<ExecuteParam> executeParamList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ArrangeParam context = bizParam.getArranges().get(i);
            if (context.getType().equals(ArrangeTypeEnum.function.name())) {
                FunctionParam functionContext = functionParamMap.get(context.getName());
                if (functionContext == null) {
                    throw UnifiedException.gen("biz: " + bizParam.getBizKey() + " 找不到 function:" + context.getName());
                }
                ExecuteParam executeParam = ExecuteParam.gen(functionContext);
                executeParam.setBizKey(bizParam.getBizKey());
                fillInOutputMap(context, executeParam);
                executeParamList.add(executeParam);
            } else if (context.getType().equals(ArrangeTypeEnum.biz.name())) {
                BizParam innerBizParam = bizParamMap.get(context.getName());
                if (innerBizParam == null) {
                    innerBizParam = ArrangeContext.bizParamMap.get(context.getName());
                }
                if (innerBizParam == null) {
                    throw UnifiedException.gen("biz: " + bizParam.getBizKey() + " 找不到 biz:" + context.getName());
                }
                if (currentBizKeys.contains(innerBizParam.getBizKey())) {
                    currentBizKeys.add(innerBizParam.getBizKey());
                    throw UnifiedException.gen("有循环依赖 " + JsonUtil.bean2json(currentBizKeys));
                }
                initExecuteChains(innerBizParam, bizParamMap);
                List<ExecuteParam> executeParams = Lists.newArrayList(bizParamMap.get(context.getName()).getExecuteChains());
                ExecuteParam executeParam = executeParams.get(0);
                fillInOutputMap(context, executeParam);
                executeParamList.addAll(executeParams);
            }
        }
        bizParam.setExecuteChains(executeParamList);
    }

    private static void fillInOutputMap(ArrangeParam context, ExecuteParam executeParam) {
        if (EmptyUtil.isNotEmpty(context.getInput())) {
            for (String input : context.getInput()) {
                List<String> inOutput = SplitUtil.split(input, Constants.input_split, (t) -> t);
                if (inOutput.size() != 2) {
                    throw UnifiedException.gen("biz: " + executeParam.getBizKey() + " input:" + input + "不合法");
                }
                executeParam.putInOutputMap(inOutput.get(0), inOutput.get(1));
            }
        }
    }

    private static Map<String, FunctionParam> initFunctions(LinkedHashMap functionDefines) {
        Map<String, FunctionParam> functionContextMap = new LinkedHashMap<>();
        if (EmptyUtil.isEmpty(functionDefines)) {
            return functionContextMap;
        }
//        log.info("function init start");
        for (Object obj : functionDefines.keySet()) {
            String key = (String) obj;
            Object value = functionDefines.get(key);
            FunctionParam functionContext = parse(JsonUtil.bean2json(value), FunctionParam.class);
            functionContext.setFunctionKey(key);
//            log.info("function {} , functionContext:{}", key, JsonUtil.bean2jsonPretty(functionContext));
            functionContext.validSelf();
            functionContextMap.put(key, functionContext);
        }
        return functionContextMap;
    }

    private static Map<String, BizParam> initBiz(LinkedHashMap bizDefines) {
        Map<String, BizParam> bizContextMap = new LinkedHashMap<>();
        if (EmptyUtil.isEmpty(bizDefines)) {
            return bizContextMap;
        }
//        log.info("biz init start");
        for (Object obj : bizDefines.keySet()) {
            String key = (String) obj;
            Object value = bizDefines.get(key);
            BizParam bizContext = parse(JsonUtil.bean2json(value), BizParam.class);
            bizContext.setBizKey(key);
//            log.info("biz {} , bizContext:{}", key, JsonUtil.bean2jsonPretty(bizContext));
            bizContext.validSelf();
            bizContextMap.put(key, bizContext);
        }
        return bizContextMap;

    }

    private static <T> T parse(String text, Class<T> clazz) {
        NameFilter name2CamelCaseFilter = (object, name, value) -> ConvertUtil.underline2Camel(name);
        ValueFilter valueFilter = (object, name, value) -> value;
        SerializeFilter[] serializeFilters = new SerializeFilter[]{name2CamelCaseFilter, valueFilter};
        return JSON.parseObject(
                JSON.toJSONString(JSON.parse(text), serializeFilters, SerializerFeature.DisableCircularReferenceDetect),
                clazz);
    }

}
