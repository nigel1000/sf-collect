package collect.util;

import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Slf4j
public class StringUtilTest {

    public static void main(String[] args) {

        log.info("{}", Arrays.toString(StringUtil.split2Array("11::22::33:", ":")));
        log.info("{}", Arrays.toString(StringUtil.split2Array("", ":")));
        log.info("{}", Arrays.toString(StringUtil.split2Array(null, ":")));
        log.info("{}", Arrays.toString(StringUtil.split2Array("$$@#$%^&*()!!@#$%^&*()``@#$%^&*()", "@#$%^&*()")));

        log.info("{}", StringUtil.split("aa:bb:cc", ":", a -> a + "sf"));
        log.info("{}", StringUtil.split("11.01,22,33", ",", BigDecimal::new));
        log.info("{}", StringUtil.split2StringByComma("11,22,33"));
        log.info("{}", StringUtil.split2LongByComma("11,22,33"));

        log.info("{}", StringUtil.join(Arrays.asList(1, 2, 3), ":"));
        log.info("{}", StringUtil.joinByComma(Arrays.asList(1, 2, 3)));


        log.info("{}", StringUtil.format("{}---{}---{}", "$^", "aa$bb", "!@#$%^&*()_"));
        log.info("{}", StringUtil.format("{}---{}---{}", "$^", "aa$bb", "!@#$%^&*()_", new RuntimeException("测试")));

        log.info("{}", StringUtil.chars("!@#$%^&*()_"));

    }

}
