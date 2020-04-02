package com.common.collect.test.debug.arrange.context;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.ThreadLocalUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import com.common.collect.test.debug.arrange.constants.ArrangeConstants;
import com.common.collect.test.debug.arrange.model.BizDefineArrangeModel;
import com.common.collect.test.debug.arrange.model.BizDefineModel;
import com.common.collect.test.debug.arrange.model.FunctionDefineModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by hznijianfeng on 2019/7/9.
 */

@Slf4j
@Data
public class BizContext {

    private String bizKey;

    private BizDefineModel bizDefineModel;

    private List<BizFunctionChain> bizFunctionChains;

    private static Map<String, BizContext> bizContextMap = new LinkedHashMap<>();

    public static Map<String, BizContext> getBizContextMap() {
        return bizContextMap;
    }

    public static BizContext getBizContextByKey(String key) {
        BizContext bizContext = bizContextMap.get(key);
        if (bizContext == null) {
            throw UnifiedException.gen(StringUtil.format("{} 业务不存在", key));
        }
        return bizContext;
    }

    public static void add2BizContextMap(Map<String, BizDefineModel> bizDefineModelMap) {
        Map<String, BizContext> allBizContextMap = new LinkedHashMap<>(BizContext.bizContextMap);
        // 解析 biz_define 的功能链
        Map<String, BizContext> bizContextMap = initFunctionChain(bizDefineModelMap);
        allBizContextMap.putAll(bizContextMap);
        // 更新内存中的业务配置
        BizContext.bizContextMap = allBizContextMap;
        if (log.isDebugEnabled()) {
            log.debug("current biz context map :");
            log.debug("{}", JsonUtil.bean2jsonPretty(allBizContextMap));
        }
    }

    private static Map<String, BizContext> initFunctionChain(Map<String, BizDefineModel> bizDefineModelMap) {
        Map<String, BizContext> ret = new LinkedHashMap<>();
        for (Map.Entry<String, BizDefineModel> entry : bizDefineModelMap.entrySet()) {
            ThreadLocalUtil.push(ArrangeConstants.thread_local_current_biz_key, new ArrayList<>());
            BizDefineModel bizDefineModel = entry.getValue();
            // 根据 bizDefineModel 初始化功能链
            BizContext bizContext = new BizContext();
            bizContext.setBizKey(bizDefineModel.getBizKey());
            bizContext.setBizDefineModel(bizDefineModel);
            initFunctionChain(bizContext, bizDefineModelMap);
            ret.put(entry.getKey(), bizContext);
        }
        ThreadLocalUtil.clear(ArrangeConstants.thread_local_current_biz_key);
        return ret;
    }

    private static void initFunctionChain(BizContext bizContext, Map<String, BizDefineModel> bizDefineModelMap) {
        if (EmptyUtil.isNotEmpty(bizContext.getBizFunctionChains())) {
            return;
        }
        BizDefineModel bizDefineModel = bizContext.getBizDefineModel();
        String bizKey = bizContext.getBizKey();
        // 叠加当前处理的 业务 key
        List<String> currentBizKeys = ThreadLocalUtil.pull(ArrangeConstants.thread_local_current_biz_key);
        currentBizKeys.add(bizKey);

        int size = bizDefineModel.getArranges().size();
        List<BizFunctionChain> bizFunctionChains = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BizDefineArrangeModel arrangeModel = bizDefineModel.getArranges().get(i);
            if (i == 0 && arrangeModel.getType().equals(BizDefineArrangeModel.TypeEnum.function.name())) {
                if (EmptyUtil.isNotEmpty(arrangeModel.getInputMappings())) {
                    throw UnifiedException.gen(StringUtil.format("业务:{},第一个功能链的input:{}必须为空", bizKey, arrangeModel.getInputMappings()));
                }
            }
            if (arrangeModel.getType().equals(BizDefineArrangeModel.TypeEnum.function.name())) {
                // 获取功能定义
                FunctionDefineModel functionDefineModel = ConfigContext.getFunctionByKey(arrangeModel.getName());
                // 根据功能定义创建功能链
                BizFunctionChain functionChain = BizFunctionChain.gen(functionDefineModel);
                // 设置业务 key
                functionChain.setBizKey(bizKey);
                // 解析业务定义的input映射
                parseBizDefineInput(arrangeModel, functionChain, bizFunctionChains);
                // 设置功能链的路径
                functionChain.setBizKeyRoute(Arrays.asList(bizKey));
                bizFunctionChains.add(functionChain);
            } else if (arrangeModel.getType().equals(BizDefineArrangeModel.TypeEnum.biz.name())) {
                // 从当前处理中获取 BizContext 业务功能链上下文
                BizDefineModel innerBizDefineModel = bizDefineModelMap.get(arrangeModel.getName());
                if (innerBizDefineModel == null) {
                    // 从内存中获取 BizContext 业务功能链上下文
                    BizContext tempBizContext = getBizContextByKey(arrangeModel.getName());
                    innerBizDefineModel = tempBizContext.getBizDefineModel();
                }
                if (currentBizKeys.contains(innerBizDefineModel.getBizKey())) {
                    // 当前正在加载的被再次加载
                    currentBizKeys.add(innerBizDefineModel.getBizKey());
                    throw UnifiedException.gen(StringUtil.format("有循环依赖:{} ", JsonUtil.bean2json(currentBizKeys)));
                }
                // 加载子业务
                BizContext innerBizContext = new BizContext();
                innerBizContext.setBizKey(innerBizDefineModel.getBizKey());
                innerBizContext.setBizDefineModel(innerBizDefineModel);
                initFunctionChain(innerBizContext, bizDefineModelMap);
                // 拷贝一份 区分在不同 biz_define 中参数的异同
                List<BizFunctionChain> functionChains = BizFunctionChain.copy(innerBizContext.getBizFunctionChains());
                for (BizFunctionChain param : functionChains) {
                    List<String> routes = new ArrayList<>();
                    routes.add(bizKey);
                    routes.addAll(param.getBizKeyRoute());
                    param.setBizKeyRoute(routes);
                }
                // 传递biz类型的input配置到此biz对应的功能
                BizFunctionChain functionChain = functionChains.get(0);
                parseBizDefineInput(arrangeModel, functionChain, bizFunctionChains);
                bizFunctionChains.addAll(functionChains);
            }
        }
        bizContext.setBizFunctionChains(bizFunctionChains);
        currentBizKeys.remove(currentBizKeys.size() - 1);
    }

    private static void parseBizDefineInput(BizDefineArrangeModel arrangeModel, BizFunctionChain functionChain, List<BizFunctionChain> beforeFunctionChains) {
        List<String> excludes = arrangeModel.getInputExcludes();
        functionChain.setInputTypeEnum(BizFunctionChain.InputTypeEnum.valueOf(arrangeModel.getInputTypeEnum().name()));
        if (EmptyUtil.isEmpty(beforeFunctionChains)) {
            // 第一个功能的 inputType 为 none
            functionChain.setInputTypeEnum(BizFunctionChain.InputTypeEnum.none);
            if (EmptyUtil.isNotEmpty(arrangeModel.getInputMappings()) || EmptyUtil.isNotEmpty(arrangeModel.getInputExcludes())) {
                throw UnifiedException.gen(StringUtil.format("{} 的第一个功能 input_* 必须为空", functionChain.bizKeyRoutePath()));
            }
        }
        if (functionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.auto) ||
                functionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.assign)) {
            for (String input : arrangeModel.getInputMappings()) {
                List<String> inOutput = StringUtil.split2List(input, ArrangeConstants.input_split);
                if (inOutput.size() != 2) {
                    throw UnifiedException.gen(StringUtil.format("{} 的 input_mapping(lastOut->currentIn):{} 不合法", functionChain.bizKeyRoutePath(), input));
                }
                if (!excludes.contains(inOutput.get(1))) {
                    functionChain.putInOutputMap(inOutput.get(0), inOutput.get(1));
                }
            }
        }

        if (EmptyUtil.isNotEmpty(beforeFunctionChains)) {
            BizFunctionChain lastFunctionChain = beforeFunctionChains.get(beforeFunctionChains.size() - 1);
            FunctionDefineModel currentFunctionDefineModel = ConfigContext.getFunctionByKey(functionChain.getFunctionKey());
            FunctionDefineModel lastFunctionDefineModel = ConfigContext.getFunctionByKey(lastFunctionChain.getFunctionKey());
            List<String> inFields = currentFunctionDefineModel.getFunctionMethodInFields();
            List<String> lastOutFields = lastFunctionDefineModel.getFunctionMethodOutFields();

            if (functionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.auto)) {
                // 取交集 默认进行属性对应
                List<String> retain = new ArrayList<>(inFields);
                retain.retainAll(lastOutFields);
                retain.removeAll(excludes);
                for (String field : retain) {
                    functionChain.putIfAbsentInOutputMap(field, field);
                }
            }

            if (functionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.auto) ||
                    functionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.assign)) {
                Map<String, String> inOutMap = functionChain.getInOutMap();
                if (EmptyUtil.isNotEmpty(inOutMap)) {
                    for (String in : inOutMap.values()) {
                        if (!inFields.contains(in)) {
                            log.warn("当前功能的输入 input_mapping[1] 属性应该在此范围内:{}", JsonUtil.bean2json(inFields));
                            throw UnifiedException.gen(StringUtil.format("{} 的 当前功能的输入 input_mapping[1]:{} 属性设置有误", functionChain.bizKeyRoutePath(), in));
                        }
                    }
                    for (String out : inOutMap.keySet()) {
                        if (!lastOutFields.contains(out)) {
                            log.warn("上一个功能的产出 属性应该在此范围内:{}", JsonUtil.bean2json(lastOutFields));
                            throw UnifiedException.gen(StringUtil.format("{} 的 上一个功能的产出 input_mapping[0]:{} 属性设置有误", functionChain.bizKeyRoutePath(), out));
                        }
                    }
                }
            }
        }

    }
}

