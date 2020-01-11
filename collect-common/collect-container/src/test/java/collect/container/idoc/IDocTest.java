package collect.container.idoc;

import com.common.collect.api.Response;
import com.common.collect.container.idoc.IDocClient;
import com.common.collect.container.idoc.IDocField;
import com.common.collect.container.idoc.IDocMethod;
import com.common.collect.container.idoc.IDocMethodContext;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.ExceptionUtil;
import com.common.collect.util.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Slf4j
public class IDocTest {

    public static StringBuilder sb = new StringBuilder();

    public static void addLine(String line) {
        if (line == null) {
            line = "";
        }
        sb.append(line);
        sb.append("\n");
    }

    public static void addHtmlHead() {
        sb.append("<!DOCTYPE html>\n");
        sb.append("<head>\n");
        sb.append("<title>表格属性</title>\n");
        sb.append("<meta charset=\"utf-8\">\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
    }

    public static void addHtmlTail() {
        sb.append("</body>\n");
        sb.append("</html>\n");
    }

    private static String path;

    static {
        path = IDocTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "logs/idocs/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "logs/idocs/";
        }
        System.out.println(path);
    }

    public static void main(String[] args) {

        List<Class<?>> classList = ClassUtil.getClazzFromPackage("collect.container.idoc");
        if (EmptyUtil.isEmpty(classList)) {
            return;
        }
        for (Class<?> cls : classList) {
            List<IDocMethodContext> contexts = IDocClient.createIDoc(cls);
            for (IDocMethodContext context : contexts) {
//                log.info("createIDoc finish parse method,methodContext:{}",
//                        JsonUtil.bean2jsonPretty(context));
                addHtmlHead();
                ExceptionUtil.eatException(() -> Thread.sleep(5), "");
                addLine("文档链接：http://localhost:8181/doc/id/" + context.getId() + "<br>");
                addLine("文档名称：" + context.getName() + "<br>");
                addLine("文档作者：" + context.getAuthor() + "<br>");
                addLine("文档对应代码：" + context.getClassName() + "#" + context.getMethodName() + "<br>");
                addLine("访问地址：" + context.getRequestUrl() + "<br>");
                addLine("访问方式：" + context.getRequestMethod() + "<br>");
                addLine("<br>访问入参：<br>");
                addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\" align=\"left\">");
                print(context.getRequest(), 0);
                addLine("</table>");
                addLine("<br>访问返回：<br>");
                addLine("<table border=\"1\" width=\"1000\" cellspacing=\"0\" cellpadding=\"5px\" align=\"left\">");
                print(context.getResponse(), 0);
                addLine("</table>");
                addHtmlTail();
                FileUtil.createFile(path + context.getId() + ".md", false, sb.toString().getBytes(), true);
                sb = new StringBuilder();
            }
        }
    }

    public static void print(Map<String, ?> map, int level) {
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
                        String.format("<td>%s</td>", request.getType());
                if (request.getValue() instanceof Map) {
                    addLine("<tr align=\"left\">");
                    out += String.format("<td>%s</td>", "") +
                            String.format("<td>%s</td>", request.getDesc()) +
                            String.format("<td>%s</td>", request.isRequired() + "");
                    addLine(out);
                    addLine("</tr>");
                    int next = level + 1;
                    addLine("<tr align=\"left\">");
                    print((Map) request.getValue(), next);
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
                        String.format("<td>%s</td>", response.getType());
                if (response.getValue() instanceof Map) {
                    addLine("<tr align=\"left\">");
                    out += String.format("<td>%s</td>", "") +
                            String.format("<td>%s</td>", response.getDesc());
                    addLine(out);
                    addLine("</tr>");
                    int next = level + 1;
                    addLine("<tr align=\"left\">");
                    print((Map) response.getValue(), next);
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


    public static class Controller {

        @IDocMethod(id = "1", name = "测试接口", author = "hznijianfeng")
        @RequestMapping(value = "/back/door/bean/invoke", method = {RequestMethod.GET, RequestMethod.POST})
        public Response<IDocObject> idoc(
                @IDocField(nameDesc = "bean 名称", desc = "注意事项", value = "configDao")
                @RequestParam(value = "beanName")
                        String beanName,
                String methodName,
                @RequestBody IDocObject object1,
                IDocObject object2) {
            return Response.ok();
        }

    }

    @Data
    public static class IDocObject {
        @IDocField(nameDesc = "名称", desc = "小于十个字符")
        private String name;
        private String key;
        @IDocField(value = "{name:11)")
        private IDocObjectSub iDocObjectSub;

    }

    @Data
    public static class IDocObjectSub {
        @IDocField(nameDesc = "名称", desc = "小于十个字符")
        private String nameSub;
        private String keySub;
    }
}

