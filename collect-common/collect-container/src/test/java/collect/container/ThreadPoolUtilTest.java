package collect.container;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.ThreadPoolUtil;

/**
 * Created by hznijianfeng on 2019/2/28.
 */

public class ThreadPoolUtilTest {

    public static void main(String[] args) throws Exception {
        ThreadPoolUtil.exec(() -> {
            throw UnifiedException.gen("打印日志");
        });
        ThreadPoolUtil.exec(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(1000);
        System.exit(-1);
    }
}