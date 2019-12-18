package collect.util;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.CtrlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created by nijianfeng on 2019/12/3.
 */

@Slf4j
public class CtrlUtilTest {

    public static void main(String[] args) {
        try {
            CtrlUtil.retry(3, () -> {
                log.info("retry");
                if (true) {
                    throw UnifiedException.gen("retry");
                }
                return true;
            });
        } catch (UnifiedException ex) {
            log.error("", ex);
        }

        CtrlUtil.splitExecute(Arrays.asList(1, 2, 3, 4, 5, 6), 2, (t) -> log.info("{}", t));
        CtrlUtil.splitExecute(Arrays.asList(1, 2, 3, 4, 5, 6), 2, (t) -> {
            log.info("{}", (t));
            return true;
        }, true);

    }

}
