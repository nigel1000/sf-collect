package com.common.collect.container.idoc;

import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.Arrays;
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

    private Map<String, IDocFieldObj> request;
    private Map<String, IDocFieldObj> response;

    public IDocMethodContext addRequest(@NonNull IDocFieldObj value) {
        if (request == null) {
            request = new LinkedHashMap<>();
        }
        request.put(value.getName(), value);
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

    enum Type {
        request,
        response;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IDocFieldObj implements Serializable {
        // 名称
        private String name;
        private String nameDesc;
        // 类型
        private String type;
        private Class typeCls;
        private String arrayType;
        private Class arrayTypeCls;
        // 默认值
        private Object value = null;
        // 描述
        private String desc;
        // 是否必须
        private boolean required;

        private Type typeEnum;


        public void setArrayType(@NonNull Class arrayType) {
            this.arrayType = IDocUtil.typeMapping(arrayType);
            this.arrayTypeCls = arrayType;
            Object defValue = IDocUtil.typeDefaultValue(arrayType);
            if (this.value == null && defValue != null) {
                this.setValue(Arrays.asList(IDocUtil.typeDefaultValue(arrayType), IDocUtil.typeDefaultValue(arrayType)));
            }
        }

        public static IDocFieldObj of(IDocField iDocField, @NonNull Class type, @NonNull Type typeEnum) {
            IDocFieldObj docFieldObj = new IDocFieldObj();
            docFieldObj.setValue(IDocUtil.typeDefaultValue(type));
            if (iDocField != null) {
                docFieldObj.setNameDesc(iDocField.nameDesc());
                docFieldObj.setDesc(iDocField.desc());
                if (EmptyUtil.isNotEmpty(iDocField.value())) {
                    docFieldObj.setValue(iDocField.value());
                }
                if (Type.request == typeEnum) {
                    docFieldObj.setRequired(iDocField.required());
                }
            }
            docFieldObj.setType(IDocUtil.typeMapping(type));
            docFieldObj.setTypeCls(type);
            docFieldObj.setTypeEnum(typeEnum);
            return docFieldObj;
        }
    }


}
