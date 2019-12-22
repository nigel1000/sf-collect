package collect.util;

import com.common.collect.util.SqlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created by nijianfeng on 2019/12/22.
 */
@Slf4j
public class SqlUtilTest {

    public static void main(String[] args) {

        SqlUtil sqlUtil = SqlUtil.of("select * from table")
                .whereEqAnd("eqField", 1)
                .whereEqOr("eqField", 1L)
                .whereGtAnd("gtField", 1L)
                .whereGtOr("gtField", 1L)
                .whereGtEqAnd("gtEqField", 1L)
                .whereGtEqOr("gtEqField", 1L)
                .whereLtAnd("ltField", 1L)
                .whereLtOr("ltField", 1L)
                .whereLtEqAnd("ltEqField", 1L)
                .whereLtEqOr("ltEqField", 1L)
                .whereLikeAroundAnd("likeAroundField", "1")
                .whereLikeHeadAnd("likeHeadField", "1")
                .whereLikeTailAnd("likeTailField", "1")
                .whereLikeAroundOr("likeAroundField", "1")
                .whereLikeHeadOr("likeHeadField", "1")
                .whereLikeTailOr("likeTailField", "1")
                .whereInAnd("inField", Arrays.asList("1", "2"))
                .whereInOr("inField", Arrays.asList("1", "2"))
                .whereNotInAnd("notInField", Arrays.asList("1", "2"))
                .whereNotInOr("notInField", Arrays.asList("1", "2"))
                .orderBy("id asc, type desc")
                .limit(10)
                .offset(5);

        log.info("args:{},size:{}", sqlUtil.getSqlArgs(), sqlUtil.getSqlArgs().size());
        log.info(sqlUtil.getSql());

        sqlUtil = SqlUtil.of("update table")
                .set("field1", 1)
                .set("field2", 2)
                .set("field3", 3)
                .whereEqOr("eqField", 1L)
                .whereLikeHeadAnd("likeHeadField", "1")
                .whereNotInOr("notInField", Arrays.asList("1", "2"))
        ;

        log.info("args:{},size:{}", sqlUtil.getSqlArgs(), sqlUtil.getSqlArgs().size());
        log.info(sqlUtil.getSql());

    }

}
