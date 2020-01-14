package com.common.collect.container.idoc;

import com.common.collect.container.idoc.annotations.IDocField;
import com.common.collect.container.idoc.annotations.IDocFieldExclude;
import com.common.collect.container.idoc.annotations.IDocMethod;
import com.common.collect.container.idoc.context.IDocFieldObj;
import com.common.collect.container.idoc.context.IDocFieldObjFromClassContext;
import com.common.collect.container.idoc.context.IDocFieldType;
import com.common.collect.container.idoc.context.IDocMethodContext;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

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
            methodContext.setMethodName(method.getName());
            log.info("createIDoc start parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            // 解析参数
            Parameter[] parameters = method.getParameters();
            DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
            String[] parameterNames = discover.getParameterNames(method);
            for (int i = 0; i < method.getParameterCount(); i++) {
                Parameter parameter = parameters[i];
                IDocFieldObj request = handleParameter(parameter, parameterNames[i]);
                if (request == null) {
                    continue;
                }
                if (request.isObjectType()) {
                    methodContext.addRequest((Map<String, IDocFieldObj>) request.getValue());
                } else {
                    methodContext.addRequest(request);
                }
            }
            // 解析返回
            IDocFieldObjFromClassContext context = new IDocFieldObjFromClassContext(IDocFieldType.response);
            Map<String, Type> returnTypeMap = ClassUtil.getMethodReturnGenericType(method);
            context.setGenericTypeMap(returnTypeMap);
            Class retCls = method.getReturnType();
            Map<String, IDocFieldObj> responses = new LinkedHashMap<>();
            getIDocFieldObjFromClass(retCls, responses, context);
            methodContext.addResponse(responses);
            log.info("createIDoc finish parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            contexts.add(methodContext);
        }
        return contexts;
    }

    private static IDocFieldObj handleParameter(
            @NonNull Parameter parameter, @NonNull String parameterName) {
        IDocFieldExclude exclude = parameter.getAnnotation(IDocFieldExclude.class);
        if (exclude != null) {
            return null;
        }
        if (parameter.getType() == HttpServletRequest.class ||
                parameter.getType() == HttpServletResponse.class ||
                parameter.getType() == MultipartFile.class) {
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
        if (paramCls == List.class || paramCls.isArray()) {
            actualArrayCls = handleArrayType(paramCls, parameter.getParameterizedType(), request);
        }
        // 基本类型
        if (isDirectHandleType(actualArrayCls)) {
            request.setName(parameterName);
            return request;
        }
        // 简单 vo 对象
        Map<String, IDocFieldObj> requests = new LinkedHashMap<>();
        // RequestBody
//        RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
//        if (requestBody != null) {
//            request.setRequired(requestBody.required());
//        }
        request.setName(parameterName);
        getIDocFieldObjFromClass(actualArrayCls, requests, new IDocFieldObjFromClassContext(IDocFieldType.request));
        request.setValue(requests);
        return request;

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

    private static void getIDocFieldObjFromClass(
            @NonNull Class cls,
            @NonNull Map<String, IDocFieldObj> iDocFieldObjMap,
            @NonNull IDocFieldObjFromClassContext context) {
        context.enter(cls);
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
            if (fieldCls == List.class || fieldCls.isArray()) {
                actualArrayCls = handleArrayType(fieldCls, fieldType, iDocFieldObj);
            }
            if (isDirectHandleType(actualArrayCls)) {
                iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
                continue;
            }
            Map<String, IDocFieldObj> next = new LinkedHashMap<>();
            getIDocFieldObjFromClass(actualArrayCls, next, context);
            iDocFieldObj.setValue(next);
            iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
        }
        context.exit();
    }

    private static boolean isDirectHandleType(@NonNull Class cls) {
        return ClassUtil.isPrimitive(cls) ||
                cls == Object.class ||
                cls == Map.class ||
                cls == Boolean.class ||
                cls == Long.class ||
                cls == Integer.class ||
                cls == Float.class ||
                cls == Double.class ||
                cls == Byte.class ||
                cls == Short.class ||
                cls == BigDecimal.class ||
                cls == Character.class ||
                cls == String.class ||
                cls == Date.class;
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
