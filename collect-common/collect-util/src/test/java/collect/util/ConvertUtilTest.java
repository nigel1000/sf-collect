package collect.util;

import com.common.collect.util.ConvertUtil;
import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2018/8/19.
 */

@Slf4j
public class ConvertUtilTest {

    public static void main(String[] args) {

        log.info(ConvertUtil.underline2Camel("under_line"));
        log.info(ConvertUtil.underline2Camel("under_Line"));
        log.info(ConvertUtil.underline2Camel("Under_line"));
        log.info(ConvertUtil.underline2Camel("Under_Line"));

        log.info(ConvertUtil.camel2Underline("underline"));
        log.info(ConvertUtil.camel2Underline("underLine"));
        log.info(ConvertUtil.camel2Underline("UnderLine"));

        log.info(ConvertUtil.firstLower("UnderLine"));
        log.info(ConvertUtil.firstLower("underLine"));

        log.info(ConvertUtil.firstUpper("UnderLine"));
        log.info(ConvertUtil.firstUpper("underLine"));

        log.info("{}", ConvertUtil.escapeRegex("!@#$%^&*()_"));

    }

}
