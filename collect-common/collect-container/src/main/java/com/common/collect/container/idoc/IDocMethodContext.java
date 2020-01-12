package com.common.collect.container.idoc;

import com.common.collect.container.JsonUtil;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.IdUtil;
import com.common.collect.util.StringUtil;
import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

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
            this.arrayType = typeMapping(arrayType);
            this.arrayTypeCls = arrayType;
            Object defValue = typeDefaultValue(arrayType);
            if (this.value == null && defValue != null) {
                this.setValue(JsonUtil.bean2json(Arrays.asList(typeDefaultValue(arrayType), typeDefaultValue(arrayType))));
            }
        }

        public static String typeMapping(@NonNull Class cls) {
            if (ClassUtil.isPrimitive(cls)) {
                return cls.getSimpleName();
            }
            if (cls == Date.class ||
                    cls == Map.class ||
                    cls == BigDecimal.class ||
                    cls == String.class) {
                return cls.getSimpleName();
            }
            if (cls == List.class ||
                    cls.isArray()) {
                return "Array";
            }
            return "Object";
        }

        public static Object typeDefaultValue(@NonNull Class cls) {
            if (cls == Long.class || cls == long.class) {
                return Long.valueOf("20033221");
            }
            if (cls == Integer.class || cls == int.class) {
                return Integer.valueOf("4335");
            }
            if (cls == Float.class || cls == float.class) {
                return Float.valueOf("23.3");
            }
            if (cls == Double.class || cls == double.class) {
                return Double.valueOf("43.35");
            }
            if (cls == Boolean.TYPE || cls == boolean.class) {
                return Boolean.TRUE;
            }
            if (cls == Byte.TYPE || cls == byte.class) {
                return Byte.valueOf("2");
            }
            if (cls == Short.TYPE || cls == short.class) {
                return Short.valueOf("122");
            }
            if (cls == Character.TYPE || cls == char.class) {
                return 'c';
            }
            if (cls == BigDecimal.class) {
                return new BigDecimal("23.43222");
            }
            if (cls == Date.class) {
                return String.valueOf(System.currentTimeMillis());
            }
            if (cls == String.class) {
                return IdUtil.uuidHex().substring(0, 5);
            }
            return null;
        }

        public static IDocFieldObj of(IDocField iDocField, @NonNull Class type, @NonNull Type typeEnum) {
            IDocFieldObj docFieldObj = new IDocFieldObj();
            docFieldObj.setValue(typeDefaultValue(type));
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
            docFieldObj.setType(typeMapping(type));
            docFieldObj.setTypeCls(type);
            docFieldObj.setTypeEnum(typeEnum);
            return docFieldObj;
        }
    }


}
