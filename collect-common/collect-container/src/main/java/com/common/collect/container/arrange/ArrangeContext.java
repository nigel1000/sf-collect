package com.common.collect.container.arrange;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.arrange.context.BizContext;
import com.common.collect.container.arrange.context.BizFunctionChain;
import com.common.collect.container.arrange.context.ConfigContext;
import com.common.collect.container.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Data
@Slf4j
public class ArrangeContext {


    private ArrangeContext() {}

    public static ArrangeRetContext runBiz(String bizKey, String paramJson) {
        BizContext bizContext = BizContext.getBizContextByKey(bizKey);
        ArrangeRetContext retContext = new ArrangeRetContext();
        retContext.setBizKey(bizContext.getBizKey());
        List<BizFunctionChain> bizFunctionChains = bizContext.getBizFunctionChains();
        Object ret = null;
        Object arg = null;
        int i = 1;
        for (BizFunctionChain bizFunctionChain : bizFunctionChains) {
            if (bizFunctionChain.getFunctionMethodTypeEnum().equals(FunctionMethodTypeEnum.inputLessEqualOne)) {
                Class<?> paramType = null;
                if (bizFunctionChain.getParamCount() == 1) {
                    paramType = bizFunctionChain.getParamTypes()[0];
                }
                // 为 null 时表示是第一个功能
                if (paramType == null) {
                    arg = null;
                } else if (bizFunctionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.none)) {
                    if (paramJson != null) {
                        arg = JsonUtil.json2bean(paramJson, paramType);
                    }
                } else if (bizFunctionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.assign)
                        || bizFunctionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.auto)) {
                    Map<String, Object> paramMap = new HashMap<>();
                    Map<String, String> inOutMap = bizFunctionChain.getInOutMap();
                    for (Map.Entry<String, String> entry : inOutMap.entrySet()) {
                        String outField = entry.getKey();
                        String inField = entry.getValue();
                        FunctionMethodOutFromEnum outFrom = bizFunctionChain.getFunctionMethodOutFromEnum();
                        if (outFrom.equals(FunctionMethodOutFromEnum.output)) {
                            if (ret != null) {
                                paramMap.put(inField, ClassUtil.getFieldValue(ret, outField));
                            }
                        } else if (outFrom.equals(FunctionMethodOutFromEnum.input)) {
                            if (arg != null) {
                                paramMap.put(inField, ClassUtil.getFieldValue(arg, outField));
                            }
                        }
                    }
                    if (EmptyUtil.isNotEmpty(paramMap)) {
                        arg = JsonUtil.json2bean(JsonUtil.bean2json(paramMap), paramType);
                    }
                } else if (bizFunctionChain.getInputTypeEnum().equals(BizFunctionChain.InputTypeEnum.pass)) {
                    FunctionMethodOutFromEnum outFrom = bizFunctionChain.getFunctionMethodOutFromEnum();
                    if (outFrom.equals(FunctionMethodOutFromEnum.output)) {
                        // 上一个返回作为输入
                        arg = ret;
                    } else if (outFrom.equals(FunctionMethodOutFromEnum.input)) {
                        // 上一个输入作为输入
                        // arg = arg;
                    }
                }
                ret = ClassUtil.invoke(bizFunctionChain.getTarget(), bizFunctionChain.getMethod(), arg);
                if (bizFunctionChain.getFunctionInKeep()) {
                    retContext.putInputMap(bizFunctionChain.bizKeyRoutePath() + "-" + i, arg);
                }
                if (bizFunctionChain.getFunctionOutKeep()) {
                    retContext.putOutputMap(bizFunctionChain.bizKeyRoutePath() + "-" + i, ret);
                }
                i++;
            } else {
                throw UnifiedException.gen(bizFunctionChain.getTarget().getClass().getName() + "#"
                        + bizFunctionChain.getMethod().getName() + " 入参只能是一个");
            }
        }
        retContext.setLastRet(ret);
        retContext.setLastArg(arg);
        return retContext;
    }

    public synchronized static void load(Object... obj) {
        ConfigContext.load(obj);
    }

}
