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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
                IDocMethodContext.IDocFieldRequest request = handleParameter(parameter, parameterNames[i]);
                methodContext.addRequest(request);
            }
            // 解析返回
            Class retCls = method.getReturnType();
            Map<String, Class> returnTypeMap = ClassUtil.getMethodReturnGenericType(method);
            Map<String, IDocMethodContext.IDocFieldResponse> responses = new LinkedHashMap<>();
            getResponseFromClass(retCls, returnTypeMap, responses);
            methodContext.setResponse(responses);
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

    private static IDocMethodContext.IDocFieldRequest handleParameter(
            @NonNull Parameter parameter, @NonNull String parameterName) {
        // IDocField
        IDocField iDocField = parameter.getAnnotation(IDocField.class);
        IDocMethodContext.IDocFieldRequest request = IDocMethodContext.IDocFieldRequest.of(iDocField);
        if (EmptyUtil.isEmpty(request.getType())) {
            request.setType(typeMapping(parameter.getType()));
        }
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
        Map<String, IDocMethodContext.IDocFieldRequest> requests = new LinkedHashMap<>();
        RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
        if (requestBody != null) {
            request.setRequired(requestBody.required());
            request.setName(parameterName);
            getRequestFromClass(paramCls, requests);
            request.setValue(requests);
            return request;
        }

        // 简单 vo 对象
        request.setName(parameterName);
        getRequestFromClass(paramCls, requests);
        request.setValue(requests);
        return request;

    }

    private static void getRequestFromClass(
            @NonNull Class paramCls,
            @NonNull Map<String, IDocMethodContext.IDocFieldRequest> requests) {
        Field[] fields = ClassUtil.getFields(paramCls);
        for (Field field : fields) {
            // IDocField
            IDocField iDocField = field.getAnnotation(IDocField.class);
            IDocMethodContext.IDocFieldRequest request = IDocMethodContext.IDocFieldRequest.of(iDocField);
            if (EmptyUtil.isEmpty(request.getType())) {
                request.setType(typeMapping(field.getType()));
            }
            request.setName(field.getName());
            Class fieldCls = field.getType();
            if (fieldCls == List.class) {
                Class<?> actualType = ClassUtil.getFieldGenericType(field, 0);
                request.setArrayType(typeMapping(actualType));
                if (isDirectHandleType(actualType)) {
                    requests.put(request.getName(), request);
                } else {
                    Map<String, IDocMethodContext.IDocFieldRequest> next = new LinkedHashMap<>();
                    getRequestFromClass(actualType, next);
                    request.setValue(next);
                    requests.put(request.getName(), request);
                }
                continue;
            }
            if (isDirectHandleType(fieldCls)) {
                requests.put(request.getName(), request);
                continue;
            }
            Map<String, IDocMethodContext.IDocFieldRequest> next = new LinkedHashMap<>();
            getRequestFromClass(fieldCls, next);
            request.setValue(next);
            requests.put(request.getName(), request);
        }
    }

    private static void getResponseFromClass(
            @NonNull Class paramCls,
            @NonNull Map<String, Class> returnTypeMap,
            @NonNull Map<String, IDocMethodContext.IDocFieldResponse> responses) {
        Field[] fields = ClassUtil.getFields(paramCls);
        for (Field field : fields) {
            // IDocField
            IDocField iDocField = field.getAnnotation(IDocField.class);
            IDocMethodContext.IDocFieldResponse response = IDocMethodContext.IDocFieldResponse.of(iDocField);
            if (EmptyUtil.isEmpty(response.getType())) {
                response.setType(typeMapping(field.getType()));
            }
            response.setName(field.getName());
            Class fieldCls = field.getType();
            if (fieldCls == List.class) {
                Class<?> actualType = ClassUtil.getFieldGenericType(field, 0);
                response.setArrayType(typeMapping(actualType));
                if (isDirectHandleType(actualType)) {
                    responses.put(response.getName(), response);
                } else {
                    Map<String, IDocMethodContext.IDocFieldResponse> next = new LinkedHashMap<>();
                    getResponseFromClass(actualType, returnTypeMap, next);
                    response.setValue(next);
                    responses.put(response.getName(), response);
                }
                continue;
            }
            // 如果是泛型属性
            if (fieldCls == Object.class) {
                Class clazz = returnTypeMap.get(field.getGenericType().getTypeName());
                if (clazz != null) {
                    fieldCls = clazz;
                }
            }
            if (isDirectHandleType(fieldCls)) {
                responses.put(response.getName(), response);
                continue;
            }
            Map<String, IDocMethodContext.IDocFieldResponse> next = new LinkedHashMap<>();
            getResponseFromClass(fieldCls, returnTypeMap, next);
            response.setValue(next);
            responses.put(response.getName(), response);
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
