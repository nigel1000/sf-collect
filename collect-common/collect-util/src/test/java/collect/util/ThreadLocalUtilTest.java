package collect.util;

import com.common.collect.util.ExceptionUtil;
import com.common.collect.util.ThreadLocalUtil;
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
                ExceptionUtil.NoReturn noReturn = () -> Thread.sleep(sleep);
                ExceptionUtil.eatException(noReturn, false);
                log.info("thread:{},pullClear:{}", value, ThreadLocalUtil.pullClear(key));
                ExceptionUtil.eatException(noReturn, false);
                log.info("thread:{},push:{}", value, ThreadLocalUtil.push(key, Arrays.asList(value, value)));
                ExceptionUtil.eatException(noReturn, false);
                log.info("thread:{},pullClear:{}", value, ThreadLocalUtil.pullClear(key));
                ExceptionUtil.eatException(noReturn, false);
                log.info("thread:{},pull:{}", value, ThreadLocalUtil.pull(key));
            });
            t.start();
        }
    }

}
