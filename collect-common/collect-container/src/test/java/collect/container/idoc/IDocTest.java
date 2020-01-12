package collect.container.idoc;

import com.common.collect.api.Response;
import com.common.collect.container.JsonUtil;
import com.common.collect.container.idoc.IDocClient;
import com.common.collect.container.idoc.IDocField;
import com.common.collect.container.idoc.IDocMethod;
import com.common.collect.container.idoc.IDocMethodContext;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Slf4j
public class IDocTest {


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
                context.sortMap(context.getRequest());
                context.sortMap(context.getResponse());
                log.info("createIDoc finish parse method,methodContext:{}",
                        JsonUtil.bean2jsonPretty(context));
                FileUtil.createFile(path + context.getId() + ".md", false, context.toHtml().getBytes(), true);
            }
        }
    }

    public static class Controller {

        @IDocMethod(id = "1", name = "测试接口", author = "hznijianfeng", reCreate = true)
        @RequestMapping(value = "/back/door/bean/invoke", method = {RequestMethod.GET, RequestMethod.POST})
        public Response<IDocObject> idoc(
                @IDocField(nameDesc = "bean 名称", desc = "注意事项", value = "configDao")
                @RequestParam(value = "beanName")
                        String beanName,
                @RequestBody IDocObject object1,
                String methodName,
                IDocObject object2) {
            return Response.ok();
        }

    }

    @Data
    public static class IDocObject {
        @IDocField(value = "{name:11)")
        private IDocObjectSub sub;
        private List<IDocObjectSub> subs;
        @IDocField(nameDesc = "名称", desc = "小于十个字符")
        private String name;
        private String key;
        @IDocField(value = "[1,2,4]")
        private List<Long> longs;
        private List<Long> defLongs;

    }

    @Data
    public static class IDocObjectSub {
        @IDocField(nameDesc = "名称", desc = "小于十个字符")
        private String nameSub;
        private String keySub;
    }
}

