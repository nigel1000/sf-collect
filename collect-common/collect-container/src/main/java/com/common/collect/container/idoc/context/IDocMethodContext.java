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

    private String className;
    private Class cls;
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

    public void parseObjectResponse() {
        IDocFieldObj obj = this.response.get(GlobalConfig.directReturnKey);
        if (obj != null && obj.isObjectType()) {
            this.response = (Map<String, IDocFieldObj>) obj.getValue();
        }
    }

}
