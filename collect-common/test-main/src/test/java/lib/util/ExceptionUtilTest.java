package lib.util;

import com.common.collect.lib.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2020/1/9.
 */

@Slf4j
public class ExceptionUtilTest {

    public static void main(String[] args) {
        log.info("{}",ExceptionUtil.eatException(() -> Long.valueOf("12das"), null));
        ExceptionUtil.eatException(() -> Thread.sleep(199), null);
        try {
            ExceptionUtil.reThrowException(() -> Long.valueOf("afqef"), "string -> long 失败");
        }catch (Exception ex){
            log.info(ExceptionUtil.getStackTraceAsString(ex));
        }
    }

}
