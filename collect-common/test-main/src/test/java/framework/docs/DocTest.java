package framework.docs;

import com.common.collect.framework.docs.DocsClient;
import com.common.collect.framework.docs.context.DocsMethodContext;
import com.common.collect.framework.docs.view.ToHtml;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.List;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Slf4j
public class DocTest {

    private static String root = Paths.get(DocTest.class.getResource("/").getPath()).getParent().getParent().toString() + "/";

    public static void main(String[] args) {
        List<Class<?>> classList = ClassUtil.getClazzFromPackage("com.common.collect.framework.docs.demo");
        if (EmptyUtil.isEmpty(classList)) {
            return;
        }
        String to = root + "logs/docs/";
        log.info("to:\t" + to);
        FileUtil.createFile(to, true, null, false);
        for (Class<?> cls : classList) {
            List<DocsMethodContext> contexts = DocsClient.createDocs(cls);
            for (DocsMethodContext context : contexts) {
                FileUtil.createFile(to + context.getId() + "-" + context.getName() + ".md", false, ToHtml.toHtml(context).getBytes(), true);
                context.setMethod(null);
                FileUtil.createFile(to + context.getId() + "-" + context.getName() + ".json", false, JsonUtil.bean2jsonPretty(context).getBytes(), true);
            }
        }
    }

}

