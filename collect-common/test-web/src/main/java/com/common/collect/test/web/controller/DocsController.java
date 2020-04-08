package com.common.collect.test.web.controller;

import com.common.collect.framework.docs.DocsContext;
import com.common.collect.framework.docs.DocsEntrance;
import com.common.collect.framework.docs.DocsView;
import com.common.collect.framework.docs.model.InterfaceModel;
import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Controller
@RequestMapping("/back/door/docs")
public class DocsController {

    // http://localhost:8181/back/door/docs/list?pkg=com.common.collect.framework.docs.DocsDemo
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public void list(HttpServletResponse response, String pkg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>api 文档列表</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        DocsContext docsContext = DocsEntrance.createDocs(pkg);
        for (InterfaceModel anInterface : docsContext.getInterfaces()) {
            sb.append("<br>");
            String show = anInterface.getName() + "-" + anInterface.getClsName();
            sb.append(StringUtil.format("<a href=/back/door/docs/show?pkg={}&path={}>{}</a>", pkg, anInterface.getPath(), show));
        }
        sb.append("</body>\n");
        sb.append("</html>\n");
        WebUtil.exportHtml(response, sb.toString());
    }

    @RequestMapping(value = "/show", method = {RequestMethod.GET})
    public void show(HttpServletResponse response, String pkg, String path) {
        DocsContext docsContext = DocsEntrance.createDocs(pkg);
        for (InterfaceModel anInterface : docsContext.getInterfaces()) {
            if (anInterface.getPath().equals(path)) {
                WebUtil.exportHtml(response, DocsView.htmlView(anInterface, docsContext.getDataTypes()));
            }
        }

    }

}
