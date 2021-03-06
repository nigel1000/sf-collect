package com.common.collect.test.debug.arrange.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ConvertUtil;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import com.common.collect.lib.util.yaml.YamlUtil;
import com.common.collect.test.debug.arrange.constants.ArrangeConstants;
import com.common.collect.test.debug.arrange.model.BizDefineModel;
import com.common.collect.test.debug.arrange.model.FunctionDefineModel;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/7/9.
 */

@Slf4j
public class ConfigContext {

    private static Map<String, FunctionDefineModel> functionDefineModelMap = new LinkedHashMap<>();

    public static Map<String, FunctionDefineModel> getFunctionDefineModelMap() {
        return functionDefineModelMap;
    }

    public synchronized static void load(Object... obj) {
        Map<String, BizDefineModel> allBizDefineModelMap = new LinkedHashMap<>();
        for (Object o : obj) {
            LinkedHashMap content = YamlUtil.parse(o);
            // 解析 function_define
            Map<String, FunctionDefineModel> functionDefineModelMap = parseFunctionDefine((LinkedHashMap) content.get(ArrangeConstants.functions_define));
            ConfigContext.functionDefineModelMap.putAll(functionDefineModelMap);
            // 解析 biz_define
            Map<String, BizDefineModel> bizDefineModelMap = parseBizDefine((LinkedHashMap) content.get(ArrangeConstants.biz_define));
            allBizDefineModelMap.putAll(bizDefineModelMap);
        }
        if (log.isDebugEnabled()) {
            log.debug("current function context map :");
            log.debug("{}", JsonUtil.bean2jsonPretty(ConfigContext.functionDefineModelMap));
        }
        // 添加到内存中
        BizContext.add2BizContextMap(allBizDefineModelMap);
    }

    public static FunctionDefineModel getFunctionByKey(String key) {
        FunctionDefineModel functionDefineModel = functionDefineModelMap.get(key);
        if (functionDefineModel == null) {
            throw UnifiedException.gen(StringUtil.format("{} 功能不存在", key));
        }
        return functionDefineModel;
    }

    private static Map<String, FunctionDefineModel> parseFunctionDefine(LinkedHashMap functionDefines) {
        Map<String, FunctionDefineModel> functionContextMap = new LinkedHashMap<>();
        if (EmptyUtil.isEmpty(functionDefines)) {
            return functionContextMap;
        }
//        log.info("function init start");
        for (Object obj : functionDefines.keySet()) {
            String key = (String) obj;
            Object value = functionDefines.get(key);
            FunctionDefineModel functionContext = parse(JsonUtil.bean2json(value), FunctionDefineModel.class);
            functionContext.setFunctionKey(key);
//            log.info("function {} , functionContext:{}", key, JsonUtil.bean2jsonPretty(functionContext));
            functionContext.validSelf();
            functionContextMap.put(key, functionContext);
        }
        return functionContextMap;
    }


    private static Map<String, BizDefineModel> parseBizDefine(LinkedHashMap bizDefines) {
        Map<String, BizDefineModel> bizDefineModelMap = new LinkedHashMap<>();
        if (EmptyUtil.isEmpty(bizDefines)) {
            return bizDefineModelMap;
        }
//        log.info("biz init start");
        for (Object obj : bizDefines.keySet()) {
            String key = (String) obj;
            Object value = bizDefines.get(key);
            BizDefineModel bizDefineModel = parse(JsonUtil.bean2json(value), BizDefineModel.class);
            bizDefineModel.setBizKey(key);
//            log.info("biz {} , bizDefineModel:{}", key, JsonUtil.bean2jsonPretty(bizDefineModel));
            bizDefineModel.validSelf();
            bizDefineModelMap.put(key, bizDefineModel);
        }
        return bizDefineModelMap;

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
