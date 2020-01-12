package com.common.collect.container.idoc;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToHtml {

    private static StringBuilder sb;

    public static synchronized String toHtml(@NonNull IDocMethodContext context) {
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

    private static String fromString(Object str) {
        if (str == null) {
            return "";
        }
        return str.toString();
    }

    private static void map2Html(Map<String, IDocMethodContext.IDocFieldObj> map, int level) {
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
                if (IDocMethodContext.Type.request == v.getTypeEnum()) {
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
                if (IDocMethodContext.Type.request == v.getTypeEnum()) {
                    out += String.format("<td>%s</td>", v.isRequired() + "");
                }
                addLine(out);
                addLine("</tr>");
                int next = level + 1;
                addLine("<tr align=\"left\">");
                map2Html((Map<String, IDocMethodContext.IDocFieldObj>) v.getValue(), next);
                addLine("</tr>");
            } else {
                addLine("<tr align=\"left\">");
                out += String.format("<td>%s</td>", fromString(v.getValue())) +
                        String.format("<td>%s</td>", fromString(v.getDesc()));
                if (IDocMethodContext.Type.request == v.getTypeEnum()) {
                    out += String.format("<td>%s</td>", v.isRequired() + "");
                }
                addLine(out);
                addLine("</tr>");
            }
        });
    }

    private static void addLine(String line) {
        if (line == null) {
            line = "";
        }
        sb.append(line);
        sb.append("\n");
    }

    private static void addHtmlHead() {
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>接口页面</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
    }

    private static void addHtmlTail() {
        sb.append("</body>\n");
        sb.append("</html>\n");
    }

}