package collect.util;

import com.common.collect.util.IdUtil;
import com.common.collect.util.log4j.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.collections.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/10/29.
 */

@Slf4j
public class IdUtilTest {

    public static void main(String[] args) {
        log.info("{}", IdUtil.timeDiy("diy"));
        log.info("{}", IdUtil.snowflakeId());
        log.info("{}", IdUtil.snowflakeId());


        long beginTime = System.currentTimeMillis();
        List<Long> ids = new ArrayList<>();
        // 毫秒
        int ms = 50;
        long time = System.currentTimeMillis();
        long out = 0;
        int sequence = 10;
        String sequenceTail = String.join("", Collections.nCopies(sequence, "1"));
        for (; time - beginTime <= ms; ) {
            long id = IdUtil.snowflakeId();
            if (sequenceTail.equals(Long.toBinaryString(id).substring(47, 57))) {
                out++;
            }
            ids.add(id);
            time = System.currentTimeMillis();
        }
        log.info("cost time:{} ms", time - beginTime - 1);
        log.info("sequence 达到最大值次数:{}", out);
        log.info("max size:{}, list size:{} , set size:{}", ms * (2 << (sequence - 1)), ids.size(), Sets.newHashSet(ids).size());

        Slf4jUtil.setLogLevel("error");
        log.info("{}", IdUtil.timeDiy("diy"));
        log.info("{}", IdUtil.snowflakeId());
        log.info("{}", IdUtil.snowflakeId());
    }

}
