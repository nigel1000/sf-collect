package com.common.collect.container.idoc.context;

import com.common.collect.container.idoc.annotations.IDocMethod;
import com.common.collect.container.idoc.base.GlobalConfig;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Data
public class IDocMethodContext implements Serializable {

    private String className;
    private String methodName;

    private String id;
    private String name;
    private String author;
    private String requestUrl;
    private String requestMethod;
    private boolean reCreate = true;

    private Map<String, IDocFieldObj> request;
    private Map<String, IDocFieldObj> response;

    public IDocMethodContext addRequest(@NonNull IDocFieldObj value) {
        if (request == null) {
            request = new LinkedHashMap<>();
        }
        request.put(value.getName(), value);
        return this;
    }

    public IDocMethodContext addRequest(Map<String, IDocFieldObj> value) {
        if (EmptyUtil.isEmpty(value)) {
            return this;
        }
        if (request == null) {
            request = new LinkedHashMap<>();
        }
        request.putAll(value);
        return this;
    }

    public IDocMethodContext addResponse(Map<String, IDocFieldObj> response) {
        if (EmptyUtil.isEmpty(response)) {
            return this;
        }
        if (this.response == null) {
            this.response = new LinkedHashMap<>();
        }
        this.response.putAll(response);
        return this;
    }

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

    public void sortMap(Map<String, IDocFieldObj> map) {
        if (EmptyUtil.isEmpty(map)) {
            return;
        }
        Map<String, IDocFieldObj> baseMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> objMap = new LinkedHashMap<>();
        Map<String, IDocFieldObj> arrayObjMap = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (Objects.equals(v.getType(), IDocFieldValueType.Object.name())) {
                if (v.getValue() instanceof Map) {
                    sortMap((Map<String, IDocFieldObj>) v.getValue());
                }
                objMap.put(k, v);
            } else if (Objects.equals(v.getType(), IDocFieldValueType.Array.name()) &&
                    Objects.equals(v.getArrayType(), IDocFieldValueType.Object.name())) {
                if (v.getValue() instanceof Map) {
                    sortMap((Map<String, IDocFieldObj>) v.getValue());
                }
                arrayObjMap.put(k, v);
            } else {
                baseMap.put(k, v);
            }
        });
        map.clear();
        map.putAll(baseMap);
        map.putAll(objMap);
        map.putAll(arrayObjMap);
    }


}
