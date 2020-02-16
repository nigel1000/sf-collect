package lib.util;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * Created by hznijianfeng on 2019/2/28.
 */

@Slf4j
public class ThreadPoolUtilTest {

    public static void main(String[] args) throws Exception {

        ThreadPoolUtil.exec(() -> {
            log.info("exec");
        });

        Future<Integer> ret = ThreadPoolUtil.submit(() -> {
            log.info("submit");
            return 2;
        });
        log.info("submit,ret:{}", ret.get());

        ThreadPoolUtil.exec(() -> {
            throw UnifiedException.gen("打印日志");
        });

        System.exit(-1);
    }
}