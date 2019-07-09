package com.common.collect.container.arrange.strategy;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.SpringContextUtil;
import com.common.collect.container.arrange.model.FunctionDefineModel;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.StringUtil;

/**
 * Created by hznijianfeng on 2019/7/9.
 */

public class FunctionClazzFactory {

    public static Object getInstance(FunctionDefineModel functionDefineModel) {
        Object obj;
        String functionClazzKey = functionDefineModel.getFunctionClazzKey();
        switch (functionDefineModel.getFunctionClassTypeEnum()) {
            case reflect:
                obj = ClassUtil.newInstance(functionClazzKey);
                break;
            case springByName:
                obj = SpringContextUtil.getBean(functionClazzKey);
                if (obj == null) {
                    throw UnifiedException.gen(functionClazzKey + " bean 不存在");
                }
                break;
            default:
                throw UnifiedException.gen(StringUtil.format("functionClazzType 不合法，{}", functionDefineModel.getFunctionClazzType()));
        }
        return obj;
    }


}
