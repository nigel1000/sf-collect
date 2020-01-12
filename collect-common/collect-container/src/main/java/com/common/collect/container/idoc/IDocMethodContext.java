package com.common.collect.container.idoc;

import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
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
            String defValue = typeDefaultValue(arrayType);
            if (this.value == null && defValue != null) {
                this.setValue("[" + defValue + "," + defValue + "]");
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

        public static String typeDefaultValue(@NonNull Class cls) {
            if (cls == Long.class || cls == long.class) {
                return Long.valueOf("20033221").toString();
            }
            if (cls == Integer.class || cls == int.class) {
                return Integer.valueOf("4335").toString();
            }
            if (cls == Float.class || cls == float.class) {
                return Float.valueOf("23.3").toString();
            }
            if (cls == Double.class || cls == double.class) {
                return Double.valueOf("43.35").toString();
            }
            if (cls == Boolean.TYPE || cls == boolean.class) {
                return Boolean.TRUE.toString();
            }
            if (cls == Byte.TYPE || cls == byte.class) {
                return Byte.valueOf("2").toString();
            }
            if (cls == Short.TYPE || cls == short.class) {
                return Short.valueOf("122").toString();
            }
            if (cls == Character.TYPE || cls == char.class) {
                return Character.valueOf('c').toString();
            }
            if (cls == BigDecimal.class) {
                return new BigDecimal("23.43222").toString();
            }
            if (cls == Date.class) {
                return String.valueOf(System.currentTimeMillis());
            }
            if (cls == String.class) {
                return "哈哈";
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

    public static class ToHtml {

        private StringBuilder sb;

        public synchronized String toHtml(@NonNull IDocMethodContext context) {
            sb = new StringBuilder();
            addHtmlHead();
            addLine("文档链接：http://localhost:8181/doc/id/" + context.getId() + "<br>");
            addLine("文档名称：" + context.getName() + "<br>");
            addLine("文档作者：" + context.getAuthor() + "<br>");
            addLine("文档对应代码：" + context.getClassName() + "#" + context.getMethodName() + "<br>");
            addLine("访问地址：" + context.getRequestUrl() + "<br>");
            addLine("访问方式：" + context.getRequestMethod() + "<br>");
            addLine("<p>");
            addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\" align=\"left\">");
            addLine("<caption align=\"left\">访问入参</caption>");
            map2Html(context.getRequest(), 0);
            addLine("</table>");
            addLine("<p>");
            addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\" align=\"left\">");
            addLine("<caption align=\"left\">访问返回</caption>");
            map2Html(context.getResponse(), 0);
            addLine("</table>");
            addHtmlTail();
            return sb.toString();
        }

        private String fromString(Object str) {
            if (str == null) {
                return "";
            }
            return str.toString();
        }

        private void map2Html(Map<String, IDocFieldObj> map, int level) {
            if (map == null) {
                return;
            }
            List<Integer> i = new ArrayList<>();

            String temp = "";
            for (int j = 0; j < level; j++) {
                temp += "<td> </td>";
            }
            String blank = temp;
            map.forEach((k, v) -> {
                if (i.size() == 0) {
                    addLine("<tr align=\"left\">");
                    String out = blank;
                    out += String.format("<td>%s</td>", "名称") +
                            String.format("<td>%s</td>", "名称描述") +
                            String.format("<td>%s</td>", "类型") +
                            String.format("<td>%s</td>", "默认值") +
                            String.format("<td>%s</td>", "描述");
                    if (Type.request == v.getTypeEnum()) {
                        out += String.format("<td>%s</td>", "是否必填");
                    }
                    addLine(out);
                    addLine("</tr>");
                    i.add(1);
                }
                String out = blank;
                out += String.format("<td>%s</td>", fromString(v.getName())) +
                        String.format("<td>%s</td>", fromString(v.getNameDesc()));
                if (v.getArrayType() != null) {
                    out += String.format("<td>%s</td>", v.getType() + "-" + v.getArrayType());
                } else {
                    out += String.format("<td>%s</td>", v.getType());
                }
                if (v.getValue() instanceof Map) {
                    addLine("<tr align=\"left\">");
                    out += String.format("<td>%s</td>", "") +
                            String.format("<td>%s</td>", fromString(v.getDesc()));
                    if (Type.request == v.getTypeEnum()) {
                        out += String.format("<td>%s</td>", v.isRequired() + "");
                    }
                    addLine(out);
                    addLine("</tr>");
                    int next = level + 1;
                    addLine("<tr align=\"left\">");
                    map2Html((Map<String, IDocFieldObj>) v.getValue(), next);
                    addLine("</tr>");
                } else {
                    addLine("<tr align=\"left\">");
                    out += String.format("<td>%s</td>", fromString(v.getValue())) +
                            String.format("<td>%s</td>", fromString(v.getDesc()));
                    if (Type.request == v.getTypeEnum()) {
                        out += String.format("<td>%s</td>", v.isRequired() + "");
                    }
                    addLine(out);
                    addLine("</tr>");
                }
            });
        }

        private void addLine(String line) {
            if (line == null) {
                line = "";
            }
            sb.append(line);
            sb.append("\n");
        }

        private void addHtmlHead() {
            sb.append("<!DOCTYPE html>\n");
            sb.append("<head>\n");
            sb.append("<title>接口页面</title>\n");
            sb.append("<meta charset=\"utf-8\">\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
        }

        private void addHtmlTail() {
            sb.append("</body>\n");
            sb.append("</html>\n");
        }

    }

}
