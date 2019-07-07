package com.common.collect.container.arrange.param;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.SpringContextUtil;
import com.common.collect.container.arrange.enums.FunctionMethodOutFromEnum;
import com.common.collect.container.arrange.enums.FunctionMethodTypeEnum;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by nijianfeng on 2019/7/6.
 */
@Data
public class FunctionParam {

    // 功能 key
    private String functionKey;
    // 功能 描述
    private String functionDesc;
    // 是否保存输入
    private Boolean functionInKeep;
    // 是否保存返回
    private Boolean functionOutKeep;
    // 功能 类 优先使用 function_clazz_by_spring，次之 function_clazz_by_reflect
    private Object functionClazz;
    private String functionClazzBySpring;
    private String functionClazzByReflect;
    // 功能 方法
    private Method method;
    private String functionMethod;
    // 功能 方法 入参少于等于 1
    private String functionMethodType;
    private String functionMethodInClazz;
    // 入参 关键属性 可为空 必须是 function_method_clazz 的 field
    private List<String> functionMethodInFields;
    // 导出来自 入参 input | 返回 output
    private String functionMethodOutFrom;
    // 返回 关键属性 可为空 必须是 function_method_clazz 的 field，生成 json 作为 下一个 function_name 的 function_method_in_json
    private List<String> functionMethodOutFields;

    public void validSelf() {

        if (EmptyUtil.isEmpty(functionKey)) {
            throw UnifiedException.gen("FunctionContext functionKey 不能为空");
        }
        if (functionInKeep == null) {
            throw UnifiedException.gen("FunctionContext functionInKeep 不能为空");
        }
        if (functionOutKeep == null) {
            throw UnifiedException.gen("FunctionContext functionInKeep 不能为空");
        }
        if (EmptyUtil.isEmpty(functionClazzBySpring) && EmptyUtil.isEmpty(functionClazzByReflect)) {
            throw UnifiedException.gen("FunctionContext functionClazz 不能为空");
        }
        if (EmptyUtil.isEmpty(functionMethod)) {
            throw UnifiedException.gen("FunctionContext functionMethod 不能为空");
        }
        FunctionMethodTypeEnum.valueOf(functionMethodType);
        if (EmptyUtil.isNotEmpty(functionMethodOutFrom) || EmptyUtil.isNotEmpty(functionMethodOutFields)) {
            FunctionMethodOutFromEnum.valueOf(functionMethodOutFrom);
        }

        functionClazz = initFunctionClazz();
        method = initFunctionClazzMethod();

    }

    private Object initFunctionClazz() {
        if (EmptyUtil.isNotEmpty(functionClazzBySpring)) {
            Object obj = SpringContextUtil.getBean(functionClazzBySpring);
            if (obj == null) {
                throw UnifiedException.gen(functionClazzBySpring + " bean 不存在");
            }
            return obj;
        }
        return ClassUtil.newInstance(functionClazzByReflect);
    }

    private Method initFunctionClazzMethod() {
        if (functionClazz == null) {
            throw UnifiedException.gen("先初始化实例");
        }
        Method method;
        switch (FunctionMethodTypeEnum.valueOf(functionMethodType)) {
            case inputLessEqualOne:
                if (EmptyUtil.isEmpty(functionMethodInClazz)) {
                    method = ClassUtil.getDeclaredMethod(functionClazz.getClass(), functionMethod);
                } else {
                    method = ClassUtil.getDeclaredMethod(functionClazz.getClass(), functionMethod, ClassUtil.getClass(functionMethodInClazz));
                }
                int paramCount = method.getParameterCount();
                if (paramCount > 1) {
                    throw UnifiedException.gen("方法参数多余一个");
                }
                // 校验输入输出属性存在
                if (EmptyUtil.isNotEmpty(functionMethodInFields)) {
                    if (paramCount != 1) {
                        throw UnifiedException.gen(functionClazz.getClass().getName() + "#" + functionMethod + " 方法没有参数，不能设置 functionMethodInFields");
                    }
                    for (String functionMethodInField : functionMethodInFields) {
                        ClassUtil.getField(method.getParameterTypes()[0], functionMethodInField);
                    }
                }
                if (EmptyUtil.isNotEmpty(functionMethodOutFields)) {
                    if (paramCount != 1) {
                        throw UnifiedException.gen(functionClazz.getClass().getName() + "#" + functionMethod + " 方法没有参数，不能设置 functionMethodOutFields");
                    }
                    for (String functionMethodOutField : functionMethodOutFields) {
                        if (FunctionMethodOutFromEnum.valueOf(functionMethodOutFrom).equals(FunctionMethodOutFromEnum.output)) {
                            ClassUtil.getField(method.getReturnType(), functionMethodOutField);
                        } else if (FunctionMethodOutFromEnum.valueOf(functionMethodOutFrom).equals(FunctionMethodOutFromEnum.input)) {
                            ClassUtil.getField(method.getParameterTypes()[0], functionMethodOutField);
                        }
                    }
                }
                break;
            default:
                throw UnifiedException.gen(functionMethod + " 不合法");
        }
        return method;
    }

}


