package collect.api;

import com.common.collect.api.Response;
import com.common.collect.api.ResponseUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2019/3/6.
 */

@Slf4j
public class ResponseUtilTest {

    public static void main(String[] args) {

        log.info("response:{}", ResponseUtil.parse(Response.ok(22)));

        try {
            ResponseUtil.parse(Response.fail("错误"));
        } catch (Exception ex) {
            log.info("exception :", ex);
        }

    }

}
