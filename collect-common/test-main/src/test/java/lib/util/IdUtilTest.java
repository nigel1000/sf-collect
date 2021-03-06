package lib.util;

import com.common.collect.lib.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
            String binary = Long.toBinaryString(id);
            if (sequenceTail.equals(binary.substring(binary.length() - 10))) {
                out++;
            }
            ids.add(id);
            time = System.currentTimeMillis();
        }
        log.info("done");
        log.info("cost time:{} ms", time - beginTime - 1);
        log.info("sequence 达到最大值次数:{}", out);
        log.info("max size:{}, list size:{} , set size:{}", ms * (2 << (sequence - 1)), ids.size(), new HashSet<>(ids).size());

    }

}
