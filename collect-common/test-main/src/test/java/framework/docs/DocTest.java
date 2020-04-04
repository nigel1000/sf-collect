package framework.docs;

import com.common.collect.framework.docs.DocsContext;
import com.common.collect.framework.docs.DocsEntrance;
import com.common.collect.framework.docs.DocsView;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Slf4j
public class DocTest {

    private static String root = Paths.get(DocTest.class.getResource("/").getPath().contains(":")
            ? DocTest.class.getResource("/").getPath().substring(1) : DocTest.class.getResource("/").getPath())
            .getParent().getParent().toString() + "/";

    public static void main(String[] args) {
        String to = root + "logs/docs/";
        log.info("to:\t" + to);
        FileUtil.createFile(to, true, null, false);

        String pkg = "com.common.collect.framework.docs.DocsDemo";
        DocsContext docsContext = DocsEntrance.createDocs(pkg);
        if (docsContext.hasInterfaces()) {
            for (DocsContext.Interface anInterface : docsContext.getInterfaces()) {
                FileUtil.createFile(to + anInterface.getPath() + ".html", false, DocsView.htmlView(anInterface, docsContext.getDataTypes()).getBytes(), true);
            }
            FileUtil.createFile(to + pkg + "-datatype.json", false, JsonUtil.bean2jsonPretty(docsContext.getDataTypes()).getBytes(), true);
            FileUtil.createFile(to + pkg + "-interface.json", false, JsonUtil.bean2jsonPretty(docsContext.getInterfaces()).getBytes(), true);
        }
    }

}

