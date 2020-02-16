package collect.debug;

import com.common.collect.test.debug.netty.HttpFileServer;

public class HttpFileTest {

    public static void main(String[] args) throws Exception {
        int port = 10024;
        String baseDir = "D:\\hznijianfeng";
        //它有两个参数：第一个是端口，第二个是HTTP服务端的URL路径。
        new HttpFileServer().run(port, baseDir);
    }

}