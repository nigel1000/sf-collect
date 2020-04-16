package com.common.collect.test.debug.arrange.strategy;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.test.debug.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.test.debug.arrange.model.FunctionDefineModel;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/7/9.
 */

public class FunctionMethodFactory {

    public static Method getInstance(FunctionDefineModel functionDefineModel) {
        String functionMethodInClazz = functionDefineModel.getFunctionMethodInClazz();
        Object functionClazz = functionDefineModel.getFunctionClazz();
        String functionMethodName = functionDefineModel.getFunctionMethodName();
        Method method;
        switch (functionDefineModel.getFunctionMethodTypeEnum()) {
            case inputLessEqualOne:
                if (EmptyUtil.isEmpty(functionMethodInClazz)) {
                    method = ClassUtil.getDeclaredMethods(functionClazz.getClass(), functionMethodName);
                } else {
                    method = ClassUtil.getDeclaredMethods(functionClazz.getClass(), functionMethodName, ClassUtil.getClass(functionMethodInClazz));
                }
                int paramCount = method.getParameterCount();
                if (paramCount > 1) {
                    throw UnifiedException.gen("方法参数 >1 ");
                }
                // 校验输入输出属性存在
                List<String> functionMethodInFields = functionDefineModel.getFunctionMethodInFields();
                if (EmptyUtil.isNotEmpty(functionMethodInFields)) {
                    if (paramCount == 0) {
                        throw UnifiedException.gen(StringUtil.format("{}#{} 方法没有参数，不能设置 functionMethodInFields", functionClazz.getClass().getName(), functionMethodName));
                    }
                    for (String functionMethodInField : functionMethodInFields) {
                        ClassUtil.getDeclaredField(method.getParameterTypes()[0], functionMethodInField);
                    }
                }
                List<String> functionMethodOutFields = functionDefineModel.getFunctionMethodOutFields();
                FunctionMethodOutFromEnum functionMethodOutFromEnum = functionDefineModel.getFunctionMethodOutFromEnum();
                if (EmptyUtil.isNotEmpty(functionMethodOutFields)) {
                    if (paramCount == 0 && FunctionMethodOutFromEnum.input.equals(functionMethodOutFromEnum)) {
                        throw UnifiedException.gen(StringUtil.format("当functionMethodOutFrom为{}时, {}#{} 方法没有参数，不能设置 functionMethodOutFields",
                                functionMethodOutFromEnum.name(), functionClazz.getClass().getName(), functionMethodName));
                    }
                    for (String functionMethodOutField : functionMethodOutFields) {
                        if (functionMethodOutFromEnum.equals(FunctionMethodOutFromEnum.output)) {
                            ClassUtil.getDeclaredField(method.getReturnType(), functionMethodOutField);
                        } else if (functionMethodOutFromEnum.equals(FunctionMethodOutFromEnum.input)) {
                            ClassUtil.getDeclaredField(method.getParameterTypes()[0], functionMethodOutField);
                        }
                    }
                }
                break;
            default:
                throw UnifiedException.gen(StringUtil.format("functionMethodType 不合法，{}", functionDefineModel.getFunctionMethodType()));
        }
        return method;
    }


}
