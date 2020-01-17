package com.common.collect.container.idoc.context;

import com.common.collect.api.idoc.IDocMethod;
import com.common.collect.container.idoc.base.GlobalConfig;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
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
public class IDocMethodContext implements Serializable {
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

    private Map<String, IDocFieldObj> request = new LinkedHashMap<>();
    private Map<String, IDocFieldObj> response = new LinkedHashMap<>();

    public static IDocMethodContext of(@NonNull IDocMethod iDocMethod, RequestMapping requestMapping) {
        IDocMethodContext context = new IDocMethodContext();
        context.setId(iDocMethod.id());
        context.setName(iDocMethod.name());
        context.setAuthor(iDocMethod.author());
        context.setRequestUrl(iDocMethod.requestUrl());
        context.setRequestMethod(iDocMethod.requestMethod());
        if (requestMapping != null) {
            context.setRequestUrl(StringUtil.join(requestMapping.value(), ","));
            context.setRequestMethod(StringUtil.join(requestMapping.method(), ","));
        }
        // 赋值 reCreate
        if (GlobalConfig.reCreate != null) {
            context.setReCreate(GlobalConfig.reCreate);
        } else {
            context.setReCreate(iDocMethod.reCreate());
        }

        return context;
    }

    public IDocMethodContext addRequest(IDocFieldObj value) {
        if (value == null) {
            return this;
        }
        this.request.put(value.getName(), value);
        return this;
    }

    public IDocMethodContext addRequest(Map<String, IDocFieldObj> value) {
        if (EmptyUtil.isEmpty(value)) {
            return this;
        }
        this.request.putAll(value);
        return this;
    }

    public IDocMethodContext addResponse(Map<String, IDocFieldObj> response) {
        if (EmptyUtil.isEmpty(response)) {
            return this;
        }
        this.response.putAll(response);
        return this;
    }

    public IDocMethodContext addResponse(IDocFieldObj value) {
        if (value == null) {
            return this;
        }
        this.response.put(value.getName(), value);
        return this;
    }

    public Map<String, IDocFieldObj> sortRequest() {
        sort(request);
        return request;
    }

    public Map<String, IDocFieldObj> sortResponse() {
        Map<String, IDocFieldObj> actualResponse = new LinkedHashMap<>(response);
        IDocFieldObj obj = actualResponse.get(GlobalConfig.directReturnKey);
        if (obj == null) {
            return new LinkedHashMap<>();
        }
        if (obj.isObjectType() && obj.isObjectValue()) {
            actualResponse = (Map<String, IDocFieldObj>) obj.getDefValue();
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
        IDocFieldObj obj = response.get(GlobalConfig.directReturnKey);
        if (obj == null) {
            return bean;
        }
        if (obj.isBaseType() || obj.isUnKnowType() || obj.isArrayType()) {
            return obj.getDefValueMock().get(GlobalConfig.directReturnKey);
        }
        if (obj.isObjectType()) {
            if (obj.isObjectValue()) {
                Map<String, IDocFieldObj> actualResponse = (Map<String, IDocFieldObj>) obj.getDefValue();
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

    public void sort(Map<String, IDocFieldObj> map) {
        if (EmptyUtil.isEmpty(map)) {
            return;
        }
        Map<String, IDocFieldObj> unKnowMap = new LinkedHashMap<>();

        Map<String, IDocFieldObj> baseMap = new LinkedHashMap<>();

        Map<String, IDocFieldObj> objMap = new LinkedHashMap<>();

        Map<String, IDocFieldObj> arrayBaseMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> arrayObjMap = new LinkedHashMap<>();

        for (Map.Entry<String, IDocFieldObj> entry : map.entrySet()) {
            String k = entry.getKey();
            IDocFieldObj v = entry.getValue();
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
                    sort((Map<String, IDocFieldObj>) v.getDefValue());
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
                        sort((Map<String, IDocFieldObj>) v.getDefValue());
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
