package collect.debug.docs;

import com.common.collect.container.docs.DocsClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2019/5/20.
 */
@Slf4j
public class DocsClientTest {

    private static String path;

    static {
        path = DocsClientTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "logs/docs";
        } else {
            path = path.substring(0, path.indexOf("target")) + "logs/docs";
        }
    }

    public static void main(String[] args) {
        log.info("save path:{}", path);
        DocsClient docsClient = new DocsClient();
        docsClient.createDocsApi(path, "collect.debug");
    }

}
