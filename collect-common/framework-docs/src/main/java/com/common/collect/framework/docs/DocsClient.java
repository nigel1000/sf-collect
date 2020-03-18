package com.common.collect.framework.docs;

import com.common.collect.framework.docs.base.DocsFieldType;
import com.common.collect.framework.docs.base.GlobalConfig;
import com.common.collect.framework.docs.context.DocsFieldObj;
import com.common.collect.framework.docs.context.DocsFieldObjFromClassParam;
import com.common.collect.framework.docs.context.DocsMethodContext;
import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.api.docs.DocsFieldExclude;
import com.common.collect.lib.api.docs.DocsMethod;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Slf4j
@Data
public class DocsClient {

    public static List<DocsMethodContext> createDocs(@NonNull Class<?> cls) {
        List<DocsMethodContext> contexts = new ArrayList<>();
        for (Method method : ClassUtil.getMethods(cls)) {
            DocsMethodContext methodContext = handleMethod(method);
            if (methodContext == null) {
                continue;
            }
            methodContext.setClassName(cls.getSimpleName());
            methodContext.setCls(cls);
            methodContext.setMethodName(method.getName());
            methodContext.setMethod(method);
            log.info("createDocs start parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            // 解析参数
            Parameter[] parameters = method.getParameters();
            DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
            String[] parameterNames = discover.getParameterNames(method);
            if (parameterNames == null || parameterNames.length != parameters.length) {
                continue;
            }
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                DocsFieldObj request = handleRequestParam(parameter, parameterNames[i]);
                if (request == null) {
                    continue;
                }
                if (request.isObjectType()) {
                    // 入参如果是 object， 进行平铺
                    methodContext.addRequest((Map<String, DocsFieldObj>) request.getDefValue());
                } else {
                    methodContext.addRequest(request);
                }
            }
            // 解析返回
            DocsFieldObjFromClassParam context = new DocsFieldObjFromClassParam(DocsFieldType.response);
            context.setGenericTypeMap(ClassUtil.getMethodReturnGenericTypeMap(method));
            methodContext.addResponse(handleResponse(method.getReturnType(), context));
            log.info("createDocs finish parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            contexts.add(methodContext);
        }
        return contexts;
    }

    private static DocsFieldObj handleResponse(@NonNull Class cls, @NonNull DocsFieldObjFromClassParam context) {
        DocsFieldObj retFieldObj = DocsFieldObj.of(null, cls, context.getDocFieldType());
        retFieldObj.setName(GlobalConfig.directReturnKey);
        retFieldObj.setDesc("返回数据");
        if (retFieldObj.isUnKnowType()) {
            return null;
        }
        if (retFieldObj.isBaseType()) {
            return retFieldObj;
        }
        Class actualCls = cls;
        if (retFieldObj.isArrayType()) {
            actualCls = handleArrayType(cls, context.getGenericTypeMap().get(cls.getTypeParameters()[0].getName()), retFieldObj);
        }
        if (retFieldObj.isArrayBaseType()) {
            return retFieldObj;
        }
        Map<String, DocsFieldObj> objs = getDocsFieldObjFromClass(actualCls, context);
        if (EmptyUtil.isEmpty(objs)) {
            return null;
        }
        retFieldObj.setDefValue(objs);
        return retFieldObj;
    }

    private static DocsFieldObj handleRequestParam(
            @NonNull Parameter parameter,
            @NonNull String parameterName) {
        if (parameter.isAnnotationPresent(DocsFieldExclude.class)) {
            return null;
        }
        // DocsField
        DocsField docsField = parameter.getAnnotation(DocsField.class);
        DocsFieldObj request = DocsFieldObj.of(docsField, parameter.getType(), DocsFieldType.request);
        request.setName(parameterName);
        // 如果是未知类型 到此结束
        if (request.isUnKnowType()) {
            if (docsField == null) {
                return null;
            } else {
                // 有 DocsField 注解则使用注解的信息进行返回
                return request;
            }
        }
        // RequestParam
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                request.setDefValue(requestParam.defaultValue());
            }
            request.setRequired(requestParam.required());
            if (EmptyUtil.isNotEmpty(requestParam.name())) {
                request.setName(requestParam.name());
            }
            return request;
        }
        // 如果是基本类型 到此结束
        if (request.isBaseType()) {
            return request;
        }
        // 处理 object & array
        Class paramCls = parameter.getType();
        Class actualParamFinalCls = paramCls;
        if (request.isArrayType()) {
            actualParamFinalCls = handleArrayType(paramCls, parameter.getParameterizedType(), request);
        }
        if (request.isArrayBaseType()) {
            return request;
        }
        // 如果返回为空则actualArrayCls不是一个可处理的对象类型
        Map<String, DocsFieldObj> objs = getDocsFieldObjFromClass(actualParamFinalCls, new DocsFieldObjFromClassParam(DocsFieldType.request));
        if (EmptyUtil.isEmpty(objs)) {
            if (docsField == null) {
                return null;
            } else {
                // 有 DocsField 注解则使用注解的信息进行返回
                return request;
            }
        }
        request.setDefValue(objs);
        return request;
    }

    private static Map<String, DocsFieldObj> getDocsFieldObjFromClass(
            @NonNull Class cls,
            @NonNull DocsFieldObjFromClassParam context) {
        DocsFieldObj obj = DocsFieldObj.of(null, cls, context.getDocFieldType());
        // 只处理 object 类型
        if (!obj.isObjectType()) {
            return new LinkedHashMap<>();
        }
        context.enter(cls);
        Map<String, DocsFieldObj> docsFieldObjMap = new LinkedHashMap<>();
        Field[] fields = ClassUtil.getFields(cls);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.isAnnotationPresent(DocsFieldExclude.class)) {
                continue;
            }
            Class fieldActualCls = field.getType();
            //处理泛型的情况  等于 Object 的时候可能是泛型属性
            if (fieldActualCls == Object.class) {
                // 泛型 譬如 K V
                Type fieldType = field.getGenericType();
                // 泛型的实际类型
                Type genericType = context.getGenericTypeMap().get(fieldType.getTypeName());
                if (genericType != null) {
                    if (genericType instanceof ParameterizedType) {
                        fieldActualCls = (Class) ((ParameterizedType) genericType).getRawType();
                    } else {
                        fieldActualCls = (Class) genericType;
                    }
                }
            }
            // 处理 DocsField
            DocsField docsField = field.getAnnotation(DocsField.class);
            DocsFieldObj fieldObj = DocsFieldObj.of(docsField, fieldActualCls, context.getDocFieldType());
            fieldObj.setName(field.getName());
            // 如果是未知类型 到此结束
            if (fieldObj.isUnKnowType()) {
                if (docsField == null) {
                    continue;
                } else {
                    // 有 DocsField 注解则使用注解的信息进行返回
                    docsFieldObjMap.put(fieldObj.getName(), fieldObj);
                    continue;
                }
            }
            // 如果是基本类型 到此结束
            if (fieldObj.isBaseType()) {
                docsFieldObjMap.put(fieldObj.getName(), fieldObj);
                continue;
            }

            // 处理 object & array
            Class fieldActualFinalCls = fieldActualCls;
            if (fieldObj.isArrayType()) {
                fieldActualFinalCls = handleArrayType(fieldActualCls, field.getGenericType(), fieldObj);
            }
            Map<String, DocsFieldObj> objs = getDocsFieldObjFromClass(fieldActualFinalCls, context);
            if (EmptyUtil.isEmpty(objs)) {
                if (docsField == null) {
                    continue;
                } else {
                    // 有 DocsField 注解则使用注解的信息进行返回
                    docsFieldObjMap.put(fieldObj.getName(), fieldObj);
                    continue;
                }
            }
            fieldObj.setDefValue(objs);
            docsFieldObjMap.put(fieldObj.getName(), fieldObj);
        }
        context.exit();
        return docsFieldObjMap;
    }

    public static Class handleArrayType(@NonNull Class cls, Type type, DocsFieldObj docFieldObj) {
        if (cls != List.class && !cls.isArray()) {
            return null;
        }
        int arrayCount = 0;
        Class actualArrayCls = null;
        if (cls == List.class) {
            for (int i = 0; i < 100; i++) {
                if (type instanceof ParameterizedType) {
                    arrayCount++;
                    type = ((ParameterizedType) type).getActualTypeArguments()[0];
                } else {
                    actualArrayCls = (Class) type;
                    break;
                }
            }
        }
        if (cls.isArray()) {
            actualArrayCls = cls;
        }
        if (actualArrayCls != null && actualArrayCls.isArray()) {
            for (int i = 0; i < 100; i++) {
                if (actualArrayCls.getComponentType() == null) {
                    break;
                }
                actualArrayCls = actualArrayCls.getComponentType();
                arrayCount++;
            }
        }
        docFieldObj.setArrayType(actualArrayCls, arrayCount);
        return actualArrayCls;
    }

    private static DocsMethodContext handleMethod(@NonNull Method method) {
        DocsMethod docsMethod = method.getAnnotation(DocsMethod.class);
        if (docsMethod == null) {
            return null;
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        DocsMethodContext methodContext = DocsMethodContext.of(docsMethod, requestMapping);
        if (!methodContext.isReCreate()) {
            return null;
        }
        return methodContext;
    }

}
