package com.common.collect.container.idoc;

import com.common.collect.util.StringUtil;
import lombok.*;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
        private String arrayType;
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
        private String arrayType;
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

        private void map2Html(Map<String, ?> map, int level) {
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
                if (v instanceof IDocMethodContext.IDocFieldRequest) {
                    if (i.size() == 0) {
                        addLine("<tr align=\"left\">");
                        String out = blank;
                        out += String.format("<td>%s</td>", "名称") +
                                String.format("<td>%s</td>", "名称描述") +
                                String.format("<td>%s</td>", "类型") +
                                String.format("<td>%s</td>", "默认值") +
                                String.format("<td>%s</td>", "描述") +
                                String.format("<td>%s</td>", "是否必填");
                        addLine(out);
                        addLine("</tr>");
                        i.add(1);
                    }
                    IDocMethodContext.IDocFieldRequest request = (IDocMethodContext.IDocFieldRequest) v;
                    String out = blank;
                    out += String.format("<td>%s</td>", request.getName()) +
                            String.format("<td>%s</td>", request.getNameDesc()) +
                            String.format("<td>%s</td>", request.getType() + "-" + request.getArrayType());
                    if (request.getValue() instanceof Map) {
                        addLine("<tr align=\"left\">");
                        out += String.format("<td>%s</td>", "") +
                                String.format("<td>%s</td>", request.getDesc()) +
                                String.format("<td>%s</td>", request.isRequired() + "");
                        addLine(out);
                        addLine("</tr>");
                        int next = level + 1;
                        addLine("<tr align=\"left\">");
                        map2Html((Map) request.getValue(), next);
                        addLine("</tr>");
                    } else {
                        addLine("<tr align=\"left\">");
                        out += String.format("<td>%s</td>", request.getValue()) +
                                String.format("<td>%s</td>", request.getDesc()) +
                                String.format("<td>%s</td>", request.isRequired() + "");
                        addLine(out);
                        addLine("</tr>");
                    }
                }
                if (v instanceof IDocMethodContext.IDocFieldResponse) {
                    if (i.size() == 0) {
                        addLine("<tr align=\"left\">");
                        String out = blank;
                        out += String.format("<td>%s</td>", "名称") +
                                String.format("<td>%s</td>", "名称描述") +
                                String.format("<td>%s</td>", "类型") +
                                String.format("<td>%s</td>", "默认值") +
                                String.format("<td>%s</td>", "描述");
                        addLine(out);
                        addLine("</tr>");
                        i.add(1);
                    }
                    IDocMethodContext.IDocFieldResponse response = (IDocMethodContext.IDocFieldResponse) v;
                    String out = blank;
                    out += String.format("<td>%s</td>", response.getName()) +
                            String.format("<td>%s</td>", response.getNameDesc()) +
                            String.format("<td>%s</td>", response.getType() + "-" + response.getArrayType());
                    if (response.getValue() instanceof Map) {
                        addLine("<tr align=\"left\">");
                        out += String.format("<td>%s</td>", "") +
                                String.format("<td>%s</td>", response.getDesc());
                        addLine(out);
                        addLine("</tr>");
                        int next = level + 1;
                        addLine("<tr align=\"left\">");
                        map2Html((Map) response.getValue(), next);
                        addLine("</tr>");
                    } else {
                        addLine("<tr align=\"left\">");
                        out += String.format("<td>%s</td>", response.getValue()) +
                                String.format("<td>%s</td>", response.getDesc());
                        addLine(out);
                        addLine("</tr>");
                    }
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
