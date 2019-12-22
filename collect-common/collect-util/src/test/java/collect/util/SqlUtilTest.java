package collect.util;

import com.common.collect.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nijianfeng on 2019/12/22.
 */
@Slf4j
public class SqlUtilTest {

    public static void main(String[] args) {
        log.info(SqlUtil.aroundLike("like"));
        log.info(SqlUtil.tailLike("like"));
        log.info(SqlUtil.headLike("like"));

        List<Object> values = new ArrayList<>();
        String head = "select * from table";
        String where1 = SqlUtil.where(head, values, "id", 1);
        String where2 = SqlUtil.where(head + where1, values, "key", "value");
        String in = SqlUtil.in("keys", Arrays.asList("value1", "value2"));
        String paging = SqlUtil.paging("id asc, createAt desc", 2, 2);

        log.info("{}", values);
        log.info(SqlUtil.concat(head, where1, where2, "and", in, paging));

    }

}
