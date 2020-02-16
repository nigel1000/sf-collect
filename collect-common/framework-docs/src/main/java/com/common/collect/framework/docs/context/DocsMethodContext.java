package com.common.collect.framework.docs.context;

import com.common.collect.framework.docs.base.GlobalConfig;
import com.common.collect.lib.api.docs.DocsMethod;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.StringUtil;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Data
public class DocsMethodContext implements Serializable {
    private static final long serialVersionUID = -4485345031917709151L;

    private String className;
    private Class<?> cls;
    private String methodName;
    private Method method;

    private String id;
    private String name;
    private String author;
    private String requestUrl;
    private String requestMethod;
    private boolean reCreate = true;

    private Map<String, DocsFieldObj> request = new LinkedHashMap<>();
    private Map<String, DocsFieldObj> response = new LinkedHashMap<>();

    public static DocsMethodContext of(@NonNull DocsMethod docsMethod, RequestMapping requestMapping) {
        DocsMethodContext context = new DocsMethodContext();
        context.setId(docsMethod.id());
        context.setName(docsMethod.name());
        context.setAuthor(docsMethod.author());
        context.setRequestUrl(docsMethod.requestUrl());
        context.setRequestMethod(docsMethod.requestMethod());
        if (requestMapping != null) {
            context.setRequestUrl(StringUtil.join(requestMapping.value(), ","));
            context.setRequestMethod(StringUtil.join(requestMapping.method(), ","));
        }
        // 赋值 reCreate
        if (GlobalConfig.reCreate != null) {
            context.setReCreate(GlobalConfig.reCreate);
        } else {
            context.setReCreate(docsMethod.reCreate());
        }

        return context;
    }

    public DocsMethodContext addRequest(DocsFieldObj value) {
        if (value == null) {
            return this;
        }
        this.request.put(value.getName(), value);
        return this;
    }

    public DocsMethodContext addRequest(Map<String, DocsFieldObj> value) {
        if (EmptyUtil.isEmpty(value)) {
            return this;
        }
        this.request.putAll(value);
        return this;
    }

    public DocsMethodContext addResponse(Map<String, DocsFieldObj> response) {
        if (EmptyUtil.isEmpty(response)) {
            return this;
        }
        this.response.putAll(response);
        return this;
    }

    public DocsMethodContext addResponse(DocsFieldObj value) {
        if (value == null) {
            return this;
        }
        this.response.put(value.getName(), value);
        return this;
    }

    public Map<String, DocsFieldObj> sortRequest() {
        sort(request);
        return request;
    }

    public Map<String, DocsFieldObj> sortResponse() {
        Map<String, DocsFieldObj> actualResponse = new LinkedHashMap<>(response);
        DocsFieldObj obj = actualResponse.get(GlobalConfig.directReturnKey);
        if (obj == null) {
            return new LinkedHashMap<>();
        }
        if (obj.isObjectType() && obj.isObjectValue()) {
            actualResponse = (Map<String, DocsFieldObj>) obj.getDefValue();
        }
        sort(actualResponse);
        return actualResponse;
    }

    public Object genRequestMock() {
        sort(request);
        Map<String, Object> bean = new LinkedHashMap<>();
        request.forEach((k, v) -> {
            bean.putAll(v.getDefValueMock());
        });
        return bean;
    }

    public Object genResponseMock() {
        Map<String, Object> bean = new LinkedHashMap<>();
        DocsFieldObj obj = response.get(GlobalConfig.directReturnKey);
        if (obj == null) {
            return bean;
        }
        if (obj.isBaseType() || obj.isUnKnowType() || obj.isArrayType()) {
            return obj.getDefValueMock().get(GlobalConfig.directReturnKey);
        }
        if (obj.isObjectType()) {
            if (obj.isObjectValue()) {
                Map<String, DocsFieldObj> actualResponse = (Map<String, DocsFieldObj>) obj.getDefValue();
                sort(actualResponse);
                actualResponse.forEach((k, v) -> {
                    bean.putAll(v.getDefValueMock());
                });
                return bean;
            }
            return obj.getDefValueMock().get(GlobalConfig.directReturnKey);
        }
        return bean;
    }

    public void sort(Map<String, DocsFieldObj> map) {
        if (EmptyUtil.isEmpty(map)) {
            return;
        }
        Map<String, DocsFieldObj> unKnowMap = new LinkedHashMap<>();

        Map<String, DocsFieldObj> baseMap = new LinkedHashMap<>();

        Map<String, DocsFieldObj> objMap = new LinkedHashMap<>();

        Map<String, DocsFieldObj> arrayBaseMap = new LinkedHashMap<>();
        Map<String, DocsFieldObj> arrayObjMap = new LinkedHashMap<>();

        for (Map.Entry<String, DocsFieldObj> entry : map.entrySet()) {
            String k = entry.getKey();
            DocsFieldObj v = entry.getValue();
            if (v.isUnKnowType()) {
                unKnowMap.put(k, v);
                continue;
            }
            if (v.isBaseType()) {
                baseMap.put(k, v);
                continue;
            }
            if (v.isObjectType()) {
                if (v.isObjectValue()) {
                    sort((Map<String, DocsFieldObj>) v.getDefValue());
                    objMap.put(k, v);
                    continue;
                }
                unKnowMap.put(k, v);
                continue;
            }
            if (v.isArrayType()) {
                if (v.isArrayBaseType()) {
                    arrayBaseMap.put(k, v);
                    continue;
                }
                if (v.isArrayUnKnowType()) {
                    unKnowMap.put(k, v);
                    continue;
                }
                if (v.isArrayObjectType()) {
                    if (v.isObjectValue()) {
                        sort((Map<String, DocsFieldObj>) v.getDefValue());
                        arrayObjMap.put(k, v);
                        continue;
                    }
                    unKnowMap.put(k, v);
                    continue;
                }
            }
        }
        map.clear();
        map.putAll(unKnowMap);
        map.putAll(baseMap);
        map.putAll(objMap);
        map.putAll(arrayBaseMap);
        map.putAll(arrayObjMap);
    }

}
