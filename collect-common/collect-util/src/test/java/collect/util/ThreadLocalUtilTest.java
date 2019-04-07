package collect.util;

import com.common.collect.util.ThreadLocalUtil;
import com.common.collect.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by nijianfeng on 2019/4/4.
 */

@Slf4j
public class ThreadLocalUtilTest {

    public static void main(String[] args) {

        String key = "test";

        for (int i = 0; i < 3; i++) {
            final int value = i;
            final int sleep = new Random(i).nextInt(1000);
            Thread t = new Thread(() -> {
                ThreadUtil.sleep(sleep);
                log.info("thread:{},pullClear:{}", value, ThreadLocalUtil.pullClear(key));
                ThreadUtil.sleep(sleep);
                log.info("thread:{},push:{}", value, ThreadLocalUtil.push(key, Arrays.asList(value, value)));
                ThreadUtil.sleepThrow(sleep);
                log.info("thread:{},pullClear:{}", value, ThreadLocalUtil.pullClear(key));
                ThreadUtil.sleepThrow(sleep);
                log.info("thread:{},pull:{}", value, ThreadLocalUtil.pull(key));
            });
            t.start();
        }
    }

}
