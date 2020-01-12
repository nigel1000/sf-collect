package com.common.collect.web.controller;

import com.common.collect.api.Response;
import com.common.collect.container.WebUtil;
import com.common.collect.container.idoc.IDocClient;
import com.common.collect.container.idoc.IDocField;
import com.common.collect.container.idoc.IDocMethod;
import com.common.collect.container.idoc.IDocMethodContext;
import com.common.collect.container.mybatis.generator.domain.param.DomainParam;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Controller
public class IDocController {

    // http://localhost:8181/api/idoc/list?pkg=com.common.collect.web.controller.IDocController
    @RequestMapping(value = "/api/idoc/list", method = {RequestMethod.GET})
    public void list(HttpServletResponse response, String pkg) {
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(pkg);
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>api doc</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        for (Class<?> cls : classList) {
            List<IDocMethodContext> contexts = IDocClient.createIDoc(cls);
            for (IDocMethodContext context : contexts) {
                sb.append("<br>");
                String show = context.getId() + "-" + context.getName();
                sb.append(StringUtil.format("<a href=/api/idoc/show?pkg={}&id={}>{}</a>", pkg, context.getId(), show));
            }
        }
        sb.append("</body>\n");
        sb.append("</html>\n");
        WebUtil.exportHtml(response, sb.toString());
    }

    // http://localhost:8181/api/idoc/show
    @RequestMapping(value = "/api/idoc/show", method = {RequestMethod.GET})
    public void show(HttpServletResponse response, String pkg, String id) {
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(pkg);
        for (Class<?> cls : classList) {
            List<IDocMethodContext> contexts = IDocClient.createIDoc(cls);
            for (IDocMethodContext context : contexts) {
                if (context.getId().equals(id)) {
                    WebUtil.exportHtml(response, context.toHtml());
                }
            }
        }

    }

    @IDocMethod(id = "5", name = "测试接口", author = "hznijianfeng", reCreate = true)
    @RequestMapping(value = "/api/idoc/demo", method = {RequestMethod.GET, RequestMethod.POST})
    public Response<List<DomainParam>> demo(
            @IDocField(nameDesc = "显示内容", desc = "随你怎么填") String content,
            DomainParam domainParam) {
        return Response.ok(new ArrayList<>());
    }


}
