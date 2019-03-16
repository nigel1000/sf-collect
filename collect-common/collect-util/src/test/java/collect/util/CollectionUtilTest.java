package collect.util;

import com.common.collect.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/5.
 */

@Slf4j
public class CollectionUtilTest {

    public static void main(String[] args) {
        List<String> origin = Arrays.asList("1", "1", "2", "1", "1", "2");
        log.info("{}", CollectionUtil.pickRepeat(origin));
        log.info("{}", origin);
    }

}
