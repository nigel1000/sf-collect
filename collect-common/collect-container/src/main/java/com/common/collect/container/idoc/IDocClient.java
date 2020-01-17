package com.common.collect.container.idoc;

import com.common.collect.api.idoc.IDocField;
import com.common.collect.api.idoc.IDocFieldExclude;
import com.common.collect.api.idoc.IDocMethod;
import com.common.collect.container.idoc.base.GlobalConfig;
import com.common.collect.container.idoc.base.IDocFieldType;
import com.common.collect.container.idoc.base.IDocFieldValueType;
import com.common.collect.container.idoc.context.IDocFieldObj;
import com.common.collect.container.idoc.context.IDocFieldObjFromClassParam;
import com.common.collect.container.idoc.context.IDocMethodContext;
import com.common.collect.container.idoc.util.IDocUtil;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Slf4j
@Data
public class IDocClient {

    public static List<IDocMethodContext> createIDoc(@NonNull Class<?> cls) {
        List<IDocMethodContext> contexts = new ArrayList<>();
        for (Method method : ClassUtil.getMethods(cls)) {
            IDocMethodContext methodContext = handleMethod(method);
            if (methodContext == null) {
                continue;
            }
            methodContext.setClassName(cls.getSimpleName());
            methodContext.setCls(cls);
            methodContext.setMethodName(method.getName());
            methodContext.setMethod(method);
            log.info("createIDoc start parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            // 解析参数
            Parameter[] parameters = method.getParameters();
            DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
            String[] parameterNames = discover.getParameterNames(method);
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = parameters[i];
                IDocFieldObj request = handleRequestParam(parameter, parameterNames[i]);
                if (request == null) {
                    continue;
                }
                if (request.isObjectType() && request.getValue() instanceof Map) {
                    methodContext.addRequest((Map<String, IDocFieldObj>) request.getValue());
                } else {
                    methodContext.addRequest(request);
                }
            }
            // 解析返回
            IDocFieldObjFromClassParam context = new IDocFieldObjFromClassParam(IDocFieldType.response);
            context.setGenericTypeMap(ClassUtil.getMethodReturnGenericType(method));
            methodContext.addResponse(handleResponse(method.getReturnType(), context));
            log.info("createIDoc finish parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            contexts.add(methodContext);
        }
        return contexts;
    }

    private static Map<String, IDocFieldObj> handleResponse(@NonNull Class cls, @NonNull IDocFieldObjFromClassParam context) {
        if (IDocUtil.typeMapping(cls).equals(IDocFieldValueType.Object)) {
            return getIDocFieldObjFromClass(cls, context);
        } else {
            IDocFieldObj fieldObj = new IDocFieldObj();
            fieldObj.setName(GlobalConfig.directReturnKey);
            fieldObj.setType(IDocUtil.typeMapping(cls));
            fieldObj.setTypeCls(cls);
            fieldObj.setDesc("返回数据");
            fieldObj.setIDocFieldType(context.getDocFieldType());
            if (IDocUtil.typeMapping(cls).equals(IDocFieldValueType.Array)) {
                Class actualArrayCls = handleArrayType(cls, context.getGenericTypeMap().get(cls.getTypeParameters()[0].getName()), fieldObj);
                if (fieldObj.isArrayObjectType()) {
                    Map<String, IDocFieldObj> arrayObject = getIDocFieldObjFromClass(actualArrayCls, context);
                    if (EmptyUtil.isEmpty(arrayObject)) {
                        return new LinkedHashMap<>();
                    }
                    fieldObj.setValue(arrayObject);
                }
            } else {
                fieldObj.setValue(IDocUtil.typeDefaultValue(cls));
            }
            Map<String, IDocFieldObj> response = new LinkedHashMap<>();
            response.put(GlobalConfig.directReturnKey, fieldObj);
            return response;
        }
    }

    private static IDocFieldObj handleRequestParam(
            @NonNull Parameter parameter, @NonNull String parameterName) {
        IDocFieldExclude exclude = parameter.getAnnotation(IDocFieldExclude.class);
        if (exclude != null) {
            return null;
        }
        // IDocField
        IDocField iDocField = parameter.getAnnotation(IDocField.class);
        IDocFieldObj request = IDocFieldObj.of(iDocField, parameter.getType(), IDocFieldType.request);
        // RequestParam
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                request.setValue(requestParam.defaultValue());
            }
            request.setRequired(requestParam.required());
            if (EmptyUtil.isEmpty(requestParam.name())) {
                request.setName(parameterName);
            } else {
                request.setName(requestParam.name());
            }
            return request;
        }
        Class paramCls = parameter.getType();
        Class actualArrayCls = paramCls;
        if (request.isArrayType()) {
            actualArrayCls = handleArrayType(paramCls, parameter.getParameterizedType(), request);
        }
        request.setName(parameterName);
        if (request.isObjectType() || request.isArrayObjectType()) {
            // 简单 vo 对象
            Map<String, IDocFieldObj> requests = getIDocFieldObjFromClass(actualArrayCls, new IDocFieldObjFromClassParam(IDocFieldType.request));
            if (EmptyUtil.isNotEmpty(requests)) {
                request.setValue(requests);
            } else {
                if (iDocField == null) {
                    return null;
                }
            }
        }
        return request;
    }

    private static Map<String, IDocFieldObj> getIDocFieldObjFromClass(
            @NonNull Class cls,
            @NonNull IDocFieldObjFromClassParam context) {
        if (!context.canHandle(cls)) {
            return new LinkedHashMap<>();
        }
        context.enter(cls);
        Map<String, IDocFieldObj> iDocFieldObjMap = new LinkedHashMap<>();
        Field[] fields = ClassUtil.getFields(cls);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            IDocFieldExclude exclude = field.getAnnotation(IDocFieldExclude.class);
            if (exclude != null) {
                continue;
            }
            // IDocField
            IDocField iDocField = field.getAnnotation(IDocField.class);
            Class fieldCls = field.getType();
            Type fieldType = field.getGenericType();
            // 等于Object的时候可能是泛型属性
            if (fieldCls == Object.class) {
                Type genericType = context.getGenericTypeMap().get(field.getGenericType().getTypeName());
                if (genericType != null) {
                    fieldType = genericType;
                    if (genericType instanceof ParameterizedType) {
                        fieldCls = (Class) ((ParameterizedType) genericType).getRawType();
                    } else {
                        fieldCls = (Class) genericType;
                    }
                }
            }

            IDocFieldObj iDocFieldObj = IDocFieldObj.of(iDocField, fieldCls, context.getDocFieldType());
            iDocFieldObj.setName(field.getName());
            Class actualArrayCls = fieldCls;
            if (iDocFieldObj.isArrayType()) {
                actualArrayCls = handleArrayType(fieldCls, fieldType, iDocFieldObj);
            }
            if (iDocFieldObj.isObjectType() || iDocFieldObj.isArrayObjectType()) {
                Map<String, IDocFieldObj> next = getIDocFieldObjFromClass(actualArrayCls, context);
                if (EmptyUtil.isNotEmpty(next)) {
                    iDocFieldObj.setValue(next);
                } else {
                    if (iDocField == null) {
                        continue;
                    }
                }
            }
            iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
        }
        context.exit();
        return iDocFieldObjMap;
    }

    public static Class handleArrayType(@NonNull Class cls, Type type, IDocFieldObj docFieldObj) {
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

    private static IDocMethodContext handleMethod(@NonNull Method method) {
        IDocMethod iDocMethod = method.getAnnotation(IDocMethod.class);
        if (iDocMethod == null) {
            return null;
        }
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        IDocMethodContext methodContext = IDocMethodContext.of(iDocMethod, requestMapping);
        if (!methodContext.isReCreate()) {
            return null;
        }
        return methodContext;
    }

}
