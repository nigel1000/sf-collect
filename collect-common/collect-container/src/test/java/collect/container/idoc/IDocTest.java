package collect.container.idoc;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.idoc.IDocClient;
import com.common.collect.container.idoc.IDocMethodContext;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

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
        List<Class<?>> classList = ClassUtil.getClazzFromPackage("com.common.collect.container.idoc.demo");
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

}

