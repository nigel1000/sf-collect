package collect.util;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/12/3.
 */

@Slf4j
public class RetryUtilTest {

    public static void main(String[] args) {
        try {
            RetryUtil.retry(3, () -> {
                log.info("retry");
                if (true) {
                    throw UnifiedException.gen("retry");
                }
                return true;
            });
        } catch (UnifiedException ex) {
            log.error("", ex);
        }
    }

}
