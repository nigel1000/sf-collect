package collect.util;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Slf4j
public class StringUtilTest {

    public static void main(String[] args) {

        log.info("{}", StringUtil.split2List("11::22::33:", ":"));
        log.info("{}", StringUtil.split2List("", ":"));
        log.info("{}", StringUtil.split2List(null, ":"));
        log.info("{}", StringUtil.split2List("$$@#$%^&*()!!@#$%^&*()``@#$%^&*()", "@#$%^&*()"));

        log.info("{}", StringUtil.join(Arrays.asList(1, 2, 3), ":"));
        log.info("{}", StringUtil.join(Arrays.asList(1, 2, 3).toArray(), ","));

        log.info("{}", StringUtil.format("{}---{}---{}", "$^", "aa$bb", "!@#$%^&*()_"));
        log.info("{}", StringUtil.format("{}---{}---{}", "$^", "aa$bb", "!@#$%^&*()_", new RuntimeException("测试")));

        log.info("{}", StringUtil.chars("!@#$%^&*()_"));

        log.info("{}", StringUtil.fromException(UnifiedException.gen("测试")));

    }

}
