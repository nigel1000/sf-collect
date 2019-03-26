package collect.debug.netty;

import com.common.collect.debug.netty.HttpFileServer;

/**
 * Created by hznijianfeng on 2019/3/25.
 */

public class HttpFileTest {

    public static void main(String[] args) throws Exception {
        int port = 10024;
        String baseDir = "D:\\hznijianfeng";
        //它有两个参数：第一个是端口，第二个是HTTP服务端的URL路径。
        new HttpFileServer().run(port, baseDir);
    }

}
