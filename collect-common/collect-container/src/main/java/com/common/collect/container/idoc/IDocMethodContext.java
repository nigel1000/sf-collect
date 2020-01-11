package com.common.collect.container.idoc;

import com.common.collect.util.StringUtil;
import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private Map<String, IDocFieldRequest> request;
    private Map<String, IDocFieldResponse> response;

    public IDocMethodContext addRequest(@NonNull IDocFieldRequest value) {
        if (request == null) {
            request = new LinkedHashMap<>();
        }
        request.put(value.getName(), value);
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


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IDocFieldRequest implements Serializable {
        // 名称
        private String name;
        private String nameDesc;
        // 类型
        private String type;
        // 默认值
        private Object value;
        // 描述
        private String desc;
        // 是否必须
        private boolean required;

        public static IDocFieldRequest of(IDocField iDocField) {
            if (iDocField == null) {
                return new IDocFieldRequest();
            }
            IDocFieldRequest request = new IDocFieldRequest();
            request.setNameDesc(iDocField.nameDesc());
            request.setType(iDocField.type());
            request.setValue(iDocField.value());
            request.setDesc(iDocField.desc());
            request.setRequired(iDocField.required());
            return request;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IDocFieldResponse implements Serializable {
        // 名称
        private String name;
        private String nameDesc;
        // 类型
        private String type;
        // 默认值
        private Object value;
        // 描述
        private String desc;

        public static IDocFieldResponse of(IDocField iDocField) {
            if (iDocField == null) {
                return new IDocFieldResponse();
            }
            IDocFieldResponse response = new IDocFieldResponse();
            response.setNameDesc(iDocField.nameDesc());
            response.setType(iDocField.type());
            response.setValue(iDocField.value());
            response.setDesc(iDocField.desc());
            return response;
        }
    }

}
