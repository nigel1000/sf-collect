package lib.slf4j;

import com.common.collect.lib.slf4j.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/12/2.
 */

@Slf4j
public class Slf4jUtilTest {

    public static void main(String[] args) {

        log.info("");
        log.info("##########开始测试 Slf4jUtilTest");
        Slf4jUtil.printLoggerInfo(null);

        log.info("");
        log.info("###########Slf4jUtil.getLoggerFactoryClassName()");
        log.info("{}", Slf4jUtil.getLoggerFactoryClassName());

        log.info("");
        log.info("##########getLoggerNames(null)");
        log.info("{}", Slf4jUtil.getLoggerNames(null));

        log.info("");
        log.info("##########Slf4jUtil.getLoggerNames(\"Slf4jUtilTest\")");
        log.info("{}", Slf4jUtil.getLoggerNames("Slf4jUtilTest"));

        log.info("");
        log.info("##########Slf4jUtil.getLoggerNames(\"Slf4jUtil\")");
        log.info("{}", Slf4jUtil.getLoggerNames("Slf4jUtil"));

        log.info("");
        log.trace("trace log before");
        Slf4jUtil.setLogLevel("Slf4jUtilTest", "trace");
        log.trace("trace log after");

        log.info("");
        Slf4jUtil.printLoggerInfo(null);

        Slf4jUtil.clear();

    }

}
