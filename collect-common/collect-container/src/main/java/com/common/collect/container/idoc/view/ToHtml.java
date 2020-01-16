package com.common.collect.container.idoc.view;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.idoc.base.IDocFieldType;
import com.common.collect.container.idoc.util.IDocUtil;
import com.common.collect.container.idoc.context.IDocFieldObj;
import com.common.collect.container.idoc.context.IDocMethodContext;
import com.common.collect.util.EmptyUtil;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToHtml {

    public static String toHtml(@NonNull IDocMethodContext context) {
        StringBuilder sb = new StringBuilder();

        addHtmlHead(sb);
        addLine("文档链接：http://localhost:8181/doc/id/" + context.getId() + "<br>", sb);
        addLine("文档名称：" + context.getName() + "<br>", sb);
        addLine("文档作者：" + context.getAuthor() + "<br>", sb);
        addLine("文档对应代码：" + context.getClassName() + "#" + context.getMethodName() + "<br>", sb);
        addLine("访问地址：" + context.getRequestUrl() + "<br>", sb);
        addLine("访问方式：" + context.getRequestMethod() + "<br>", sb);

        addLine("访问入参", sb);
        addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\">", sb);
        map2Html(context.getRequest(), 0, sb);
        addLine("</table>", sb);


        addLine("<div>", sb);
        addLine("<pre>", sb);
        Map<String, Object> bean = new LinkedHashMap<>();
        map2Json(context.getRequest(), bean);
        addLine(JsonUtil.bean2jsonPretty(bean), sb);
        addLine("</pre>", sb);
        addLine("</div>", sb);

        addLine("访问返回", sb);
        addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\" >", sb);
        map2Html(context.getResponse(), 0, sb);
        addLine("</table>", sb);

        addLine("<div>", sb);
        addLine("<pre>", sb);
        bean = new LinkedHashMap<>();
        map2Json(context.getResponse(), bean);
        addLine(JsonUtil.bean2jsonPretty(bean), sb);
        addLine("</pre>", sb);
        addLine("</div>", sb);

        addHtmlTail(sb);
        return sb.toString();
    }

    private static void map2Json(Map<String, IDocFieldObj> docFieldObjMap, @NonNull Map<String, Object> bean) {
        if (EmptyUtil.isEmpty(docFieldObjMap)) {
            return;
        }
        docFieldObjMap.forEach((k, v) -> {
            if (v.getValue() instanceof Map) {
                Map<String, Object> sub = new LinkedHashMap<>();
                map2Json((Map<String, IDocFieldObj>) v.getValue(), sub);
                if (v.isArrayType()) {
                    bean.put(k, IDocUtil.arrayCountList(sub, v.getArrayTypeCount()));
                } else {
                    bean.put(k, sub);
                }
            } else {
                if (v.isArrayType()) {
                    bean.put(k, IDocUtil.arrayCountList(v.getValue(), v.getArrayTypeCount()));
                } else {
                    bean.put(k, v.getValue());
                }
            }
        });
    }

    private static void map2Html(Map<String, IDocFieldObj> map, int level, StringBuilder sb) {
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
                addLine("<tr align=\"left\">", sb);
                String out = blank;
                out += String.format("<td>%s</td>", "名称") +
                        String.format("<td>%s</td>", "类型") +
                        String.format("<td>%s</td>", "默认值") +
                        String.format("<td>%s</td>", "描述");
                if (IDocFieldType.request == v.getIDocFieldType()) {
                    out += String.format("<td>%s</td>", "是否必填");
                }
                addLine(out, sb);
                addLine("</tr>", sb);
                i.add(1);
            }
            String out = blank;
            out += String.format("<td>%s</td>", IDocUtil.convert2String(v.getName()));
            if (v.getArrayType() != null) {
                out += String.format("<td>%s</td>", v.getType() + "-" + v.getArrayType());
            } else {
                out += String.format("<td>%s</td>", v.getType());
            }

            addLine("<tr align=\"left\">", sb);
            if (v.getValue() instanceof Map) {
                out += String.format("<td>%s</td>", "") +
                        String.format("<td>%s</td>", IDocUtil.convert2String(v.getDesc()));
            } else {
                if (v.isArrayType() && !v.isArrayObjectType()) {
                    out += String.format("<td>%s</td>", IDocUtil.convert2String(IDocUtil.arrayCountList(v.getValue(), v.getArrayTypeCount())));
                } else {
                    out += String.format("<td>%s</td>", IDocUtil.convert2String(v.getValue()));
                }
                out += String.format("<td>%s</td>", IDocUtil.convert2String(v.getDesc()));
            }
            if (IDocFieldType.request == v.getIDocFieldType()) {
                out += String.format("<td>%s</td>", v.isRequired() + "");
            }
            addLine(out, sb);
            addLine("</tr>", sb);

            if (v.getValue() instanceof Map) {
                int next = level + 1;
                addLine("<tr align=\"left\">", sb);
                map2Html((Map<String, IDocFieldObj>) v.getValue(), next, sb);
                addLine("</tr>", sb);
            }
        });
    }

    private static void addLine(String line, StringBuilder sb) {
        if (line == null) {
            line = "";
        }
        sb.append(line);
        sb.append("\n");
    }

    private static void addHtmlHead(StringBuilder sb) {
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>接口页面</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
    }

    private static void addHtmlTail(StringBuilder sb) {
        sb.append("</body>\n");
        sb.append("</html>\n");
    }

}