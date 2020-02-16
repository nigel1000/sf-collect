package com.common.collect.test.web.controller;

import com.common.collect.framework.docs.DocsClient;
import com.common.collect.framework.docs.context.DocsMethodContext;
import com.common.collect.framework.docs.view.ToHtml;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Controller
@RequestMapping("/back/door/docs")
public class DocsController {

    // http://localhost:8181/back/door/docs/list?pkg=com.common.collect.framework.docs.demo
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public void list(HttpServletResponse response, String pkg) {
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(pkg);
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>api 文档列表</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        for (Class<?> cls : classList) {
            List<DocsMethodContext> contexts = DocsClient.createDocs(cls);
            for (DocsMethodContext context : contexts) {
                sb.append("<br>");
                String show = context.getId() + "-" + context.getName();
                sb.append(StringUtil.format("<a href=/back/door/docs/show?pkg={}&id={}>{}</a>", pkg, context.getId(), show));
            }
        }
        sb.append("</body>\n");
        sb.append("</html>\n");
        WebUtil.exportHtml(response, sb.toString());
    }

    @RequestMapping(value = "/show", method = {RequestMethod.GET})
    public void show(HttpServletResponse response, String pkg, String id) {
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(pkg);
        for (Class<?> cls : classList) {
            List<DocsMethodContext> contexts = DocsClient.createDocs(cls);
            for (DocsMethodContext context : contexts) {
                if (context.getId().equals(id)) {
                    WebUtil.exportHtml(response, ToHtml.toHtml(context));
                }
            }
        }

    }

}
