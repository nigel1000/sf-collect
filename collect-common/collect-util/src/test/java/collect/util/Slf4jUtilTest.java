package collect.util;

import com.common.collect.util.log4j.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/12/2.
 */

@Slf4j
public class Slf4jUtilTest {

    public static void main(String[] args) {

        Slf4jUtil.refresh();
        log.info("开始测试 Slf4jUtilTest");
        Slf4jUtil.printLoggerInfo(null);
        log.info("Slf4jUtil.getLoggerFactoryClassName():{}", Slf4jUtil.getLoggerFactoryClassName());
        log.info("getLoggerNames(null):{}", Slf4jUtil.getLoggerNames(null));
        log.info("Slf4jUtil.getLoggerNames(\"Slf4jUtilTest\"):{}", Slf4jUtil.getLoggerNames("Slf4jUtilTest"));
        log.info("Slf4jUtil.getLoggerNames(\"Slf4jUtil\"):{}", Slf4jUtil.getLoggerNames("Slf4jUtil"));

        Slf4jUtil.setLogLevel("Slf4jUtilTest", "trace");
        Slf4jUtil.printLoggerInfo(null);
        Slf4jUtil.clear();

    }

}
