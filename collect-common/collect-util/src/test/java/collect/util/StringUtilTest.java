package collect.util;

import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Slf4j
public class StringUtilTest {

    public static void main(String[] args) {

        log.info("{}", StringUtil.format("{}---{}---{}", "$^", "aa$bb", "!@#$%^&*()_"));
        log.info("{}", StringUtil.split2CharList("!@#$%^&*()_"));
        log.info("{}", StringUtil.replaceAll("{", "{}", "!@#$%^&*()_"));
        log.info("{}", StringUtil.escapeRegex("!@#$%^&*()_"));

    }

}
