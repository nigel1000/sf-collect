package collect.util;

import com.common.collect.util.SplitUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

@Slf4j
public class SplitUtilTest {

    public static void main(String[] args) {

        log.info("{}", Arrays.toString(SplitUtil.split2Array("11::22::33:", ":")));
        log.info("{}", Arrays.toString(SplitUtil.split2Array("", ":")));
        log.info("{}", Arrays.toString(SplitUtil.split2Array(null, ":")));

        log.info("{}", SplitUtil.split("aa:bb:cc", ":", a -> a + "sf"));
        log.info("{}", SplitUtil.split("11.01,22,33", ",", BigDecimal::new));
        log.info("{}", SplitUtil.split2StringByComma("11,22,33"));
        log.info("{}", SplitUtil.split2LongByComma("11,22,33"));

        log.info("{}", SplitUtil.join(Arrays.asList(1, 2, 3), ":"));
        log.info("{}", SplitUtil.joinByComma(Arrays.asList(1, 2, 3)));

        SplitUtil.splitExecute(Arrays.asList(1, 2, 3, 4, 5, 6), 2, (t) -> log.info("{}", t));
        SplitUtil.splitExecute(Arrays.asList(1, 2, 3, 4, 5, 6), 2, (t) -> {
            log.info("{}", (t));
            return true;
        }, true);

    }

}
