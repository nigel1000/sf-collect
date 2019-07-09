package com.common.collect.container.arrange.context;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.arrange.constants.Constants;
import com.common.collect.container.arrange.model.BizDefineArrangeModel;
import com.common.collect.container.arrange.model.BizDefineModel;
import com.common.collect.container.arrange.model.FunctionDefineModel;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.SplitUtil;
import com.common.collect.util.StringUtil;
import com.common.collect.util.ThreadLocalUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        // 验证全部的业务定义是否配置正确
        validAllBizContext(allBizContextMap);
        // 更新内存中的业务配置
        BizContext.bizContextMap = allBizContextMap;
        log.info("current biz context map :");
        log.info("{}", JsonUtil.bean2jsonPretty(allBizContextMap));
    }

    private static void validAllBizContext(Map<String, BizContext> allBizContextMap) {
        for (Map.Entry<String, BizContext> entry : allBizContextMap.entrySet()) {
            BizContext bizContext = entry.getValue();
            List<BizFunctionChain> bizFunctionChains = bizContext.getBizFunctionChains();
            BizFunctionChain lastFunctionChain = null;
            for (BizFunctionChain bizFunctionChain : bizFunctionChains) {
                FunctionDefineModel functionDefineModel = ConfigContext.getFunctionByKey(bizFunctionChain.getFunctionKey());
                if (lastFunctionChain != null) {
                    List<String> inFields = functionDefineModel.getFunctionMethodInFields();
                    List<String> outFields = functionDefineModel.getFunctionMethodOutFields();
                    Map<String, String> inOutMap = bizFunctionChain.getInOutMap();
                    if (EmptyUtil.isNotEmpty(inOutMap)) {
                        for (String in : inOutMap.values()) {
                            if (!inFields.contains(in)) {
                                log.warn("属性应该在此范围内:{}", JsonUtil.bean2json(inFields));
                                throw UnifiedException.gen(StringUtil.format("{} 的 input:{} 属性设置有误", SplitUtil.join(bizFunctionChain.getBizKeyRoute(), "#"), in));
                            }
                        }
                        for (String out : inOutMap.keySet()) {
                            if (!outFields.contains(out)) {
                                log.warn("属性应该在此范围内:{}", JsonUtil.bean2json(outFields));
                                throw UnifiedException.gen(StringUtil.format("{} 的 input:{} 属性设置有误", SplitUtil.join(bizFunctionChain.getBizKeyRoute(), "#"), out));
                            }
                        }
                    }
                }
                lastFunctionChain = bizFunctionChain;
            }
        }
    }

    private static Map<String, BizContext> initFunctionChain(Map<String, BizDefineModel> bizDefineModelMap) {
        Map<String, BizContext> ret = new LinkedHashMap<>();
        for (Map.Entry<String, BizDefineModel> entry : bizDefineModelMap.entrySet()) {
            ThreadLocalUtil.push(Constants.thread_local_current_biz_key, new ArrayList<>());
            BizDefineModel bizDefineModel = entry.getValue();
            // 根据 bizDefineModel 初始化功能链
            BizContext bizContext = new BizContext();
            bizContext.setBizKey(bizDefineModel.getBizKey());
            bizContext.setBizDefineModel(bizDefineModel);
            initFunctionChain(bizContext, bizDefineModelMap);
            ret.put(entry.getKey(), bizContext);
        }
        ThreadLocalUtil.clear(Constants.thread_local_current_biz_key);
        return ret;
    }

    private static void initFunctionChain(BizContext bizContext, Map<String, BizDefineModel> bizDefineModelMap) {
        if (EmptyUtil.isNotEmpty(bizContext.getBizFunctionChains())) {
            return;
        }
        BizDefineModel bizDefineModel = bizContext.getBizDefineModel();
        String bizKey = bizContext.getBizKey();
        // 叠加当前处理的 业务 key
        List<String> currentBizKeys = ThreadLocalUtil.pull(Constants.thread_local_current_biz_key);
        currentBizKeys.add(bizKey);

        int size = bizDefineModel.getArranges().size();
        List<BizFunctionChain> bizFunctionChains = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            BizDefineArrangeModel arrangeModel = bizDefineModel.getArranges().get(i);
            if (i == 0 && arrangeModel.getType().equals(BizDefineArrangeModel.TypeEnum.function.name())) {
                if (EmptyUtil.isNotEmpty(arrangeModel.getInputMapping())) {
                    throw UnifiedException.gen(StringUtil.format("业务:{},第一个功能链的input:{}必须为空", bizKey, arrangeModel.getInputMapping()));
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
                parseBizDefineInput(arrangeModel, functionChain);
                // 设置功能链的路径
                functionChain.setBizKeyRoute(Lists.newArrayList(bizKey));
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
                BizFunctionChain functionChain = functionChains.get(0);
                parseBizDefineInput(arrangeModel, functionChain);
                for (BizFunctionChain param : functionChains) {
                    List<String> routes = new ArrayList<>();
                    routes.add(bizKey);
                    routes.addAll(param.getBizKeyRoute());
                    param.setBizKeyRoute(routes);
                }
                bizFunctionChains.addAll(functionChains);
            }
        }
        bizContext.setBizFunctionChains(bizFunctionChains);
        currentBizKeys.remove(currentBizKeys.size() - 1);
    }

    private static void parseBizDefineInput(BizDefineArrangeModel arrangeModel, BizFunctionChain functionChain) {
        List<String> excludes = arrangeModel.getInputExclude();
        for (String input : arrangeModel.getInputMapping()) {
            List<String> inOutput = SplitUtil.split(input, Constants.input_split, (t) -> t);
            if (inOutput.size() != 2) {
                throw UnifiedException.gen("biz: " + functionChain.getBizKey() + " input:" + input + "不合法");
            }
            if (!excludes.contains(inOutput.get(1))) {
                functionChain.putInOutputMap(inOutput.get(0), inOutput.get(1));
            }
        }
        if (arrangeModel.getInputTypeEnum().equals(BizDefineArrangeModel.InputTypeEnum.auto)) {
            FunctionDefineModel functionDefineModel = ConfigContext.getFunctionByKey(functionChain.getFunctionKey());
            List<String> inFields = functionDefineModel.getFunctionMethodInFields();
            List<String> outFields = functionDefineModel.getFunctionMethodOutFields();
            // 取交集 默认进行属性对应
            List<String> retain = new ArrayList<>(inFields);
            retain.retainAll(outFields);
            retain.removeAll(excludes);
            for (String field : retain) {
                functionChain.putIfAbsentInOutputMap(field, field);
            }
        }
    }
}

