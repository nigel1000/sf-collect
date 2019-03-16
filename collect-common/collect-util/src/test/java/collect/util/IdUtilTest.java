package collect.util;

import com.common.collect.util.IdUtil;
import com.common.collect.util.log4j.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2018/10/29.
 */

@Slf4j
public class IdUtilTest {

    public static void main(String[] args) {
        log.info("{}", IdUtil.timeDiy("diy"));
        log.info("{}", IdUtil.snowflakeId());
        log.info("{}", IdUtil.snowflakeId());
        Slf4jUtil.setLogLevel("error");
        log.info("{}", IdUtil.timeDiy("diy"));
        log.info("{}", IdUtil.snowflakeId());
        log.info("{}", IdUtil.snowflakeId());
    }

}
