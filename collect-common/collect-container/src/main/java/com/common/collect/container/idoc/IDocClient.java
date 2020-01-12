package com.common.collect.container.idoc;

import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.*;
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
                if (parameter.getType() == HttpServletRequest.class ||
                        parameter.getType() == HttpServletResponse.class ||
                        parameter.getType() == MultipartFile.class) {
                    continue;
                }
                IDocFieldObj request = handleParameter(parameter, parameterNames[i]);
                if (request == null) {
                    continue;
                }
                methodContext.addRequest(request);
            }
            // 解析返回
            Class retCls = method.getReturnType();
            Map<String, Type> returnTypeMap = ClassUtil.getMethodReturnGenericType(method);
            Map<String, IDocFieldObj> responses = new LinkedHashMap<>();
            getIDocFieldObjFromClass(retCls, responses, IDocFieldType.response, returnTypeMap);
            methodContext.addResponse(responses);
            log.info("createIDoc finish parse method,className:{}, methodName:{}",
                    methodContext.getClassName(), methodContext.getMethodName());
            contexts.add(methodContext);
        }
        return contexts;
    }

    private static String typeMapping(@NonNull Class cls) {
        if (ClassUtil.isPrimitive(cls)) {
            return cls.getSimpleName();
        }
        if (cls == Date.class ||
                cls == Map.class ||
                cls == String.class) {
            return cls.getSimpleName();
        }
        if (cls == List.class) {
            return "Array";
        }
        return "Object";
    }

    private static IDocFieldObj handleParameter(
            @NonNull Parameter parameter, @NonNull String parameterName) {
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
        // 基本类型
        Class paramCls = parameter.getType();
        if (isDirectHandleType(paramCls)) {
            request.setName(parameterName);
            return request;
        }
        // RequestBody
        Map<String, IDocFieldObj> requests = new LinkedHashMap<>();
        RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
        if (requestBody != null) {
            request.setRequired(requestBody.required());
            request.setName(parameterName);
            getIDocFieldObjFromClass(paramCls, requests, IDocFieldType.request, new LinkedHashMap<>());
            request.setValue(requests);
            return request;
        }

        // 简单 vo 对象
        request.setName(parameterName);
        getIDocFieldObjFromClass(paramCls, requests, IDocFieldType.request, new LinkedHashMap<>());
        request.setValue(requests);
        return request;

    }

    private static void getIDocFieldObjFromClass(
            @NonNull Class cls,
            @NonNull Map<String, IDocFieldObj> iDocFieldObjMap,
            @NonNull IDocFieldType iDocFieldType,
            @NonNull Map<String, Type> returnTypeMap) {
        Field[] fields = ClassUtil.getFields(cls);
        for (Field field : fields) {
            // IDocField
            IDocField iDocField = field.getAnnotation(IDocField.class);
            Class fieldCls = field.getType();
            Class<?> actualType = null;
            // 如果是泛型属性
            if (iDocFieldType == IDocFieldType.response && fieldCls == Object.class) {
                Type type = returnTypeMap.get(field.getGenericType().getTypeName());
                if (type != null) {
                    if (type instanceof ParameterizedType) {
                        if (((ParameterizedType) type).getRawType() == List.class) {
                            fieldCls = List.class;
                            actualType = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                        } else {
                            continue;
                        }
                    } else {
                        fieldCls = (Class) type;
                    }
                }
            }

            IDocFieldObj iDocFieldObj = IDocFieldObj.of(iDocField, fieldCls, iDocFieldType);
            iDocFieldObj.setName(field.getName());
            if (fieldCls == List.class) {
                if (actualType == null) {
                    actualType = ClassUtil.getFieldGenericType(field, 0);
                }
                iDocFieldObj.setArrayType(actualType);
                if (isDirectHandleType(actualType)) {
                    iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
                } else {
                    Map<String, IDocFieldObj> next = new LinkedHashMap<>();
                    getIDocFieldObjFromClass(actualType, next, iDocFieldType, returnTypeMap);
                    iDocFieldObj.setValue(next);
                    iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
                }
                continue;
            }
            if (isDirectHandleType(fieldCls)) {
                iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
                continue;
            }
            Map<String, IDocFieldObj> next = new LinkedHashMap<>();
            getIDocFieldObjFromClass(fieldCls, next, iDocFieldType, returnTypeMap);
            iDocFieldObj.setValue(next);
            iDocFieldObjMap.put(iDocFieldObj.getName(), iDocFieldObj);
        }
    }

    private static boolean isDirectHandleType(Class cls) {
        return ClassUtil.isPrimitive(cls) ||
                cls == Object.class ||
                cls == Date.class ||
                cls == Map.class ||
                cls == String.class;
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
