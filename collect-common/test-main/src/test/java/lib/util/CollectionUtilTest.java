package lib.util;

import com.common.collect.lib.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2018/9/5.
 */

@Slf4j
public class CollectionUtilTest {

    public static void main(String[] args) {
        List<String> origin = new ArrayList<>(Arrays.asList("1", "1", "2", "1", "1", "2", "3", ""));
        origin.add(null);
        origin.add(null);
        log.info("removeBlank:{}", CollectionUtil.removeBlank(origin));
        log.info("removeNull:{}", CollectionUtil.removeNull(origin));
        log.info("pickRepeat:{}", CollectionUtil.pickRepeat(origin));
        log.info("removeDuplicate:{}", CollectionUtil.distinct(origin));
    }

}
