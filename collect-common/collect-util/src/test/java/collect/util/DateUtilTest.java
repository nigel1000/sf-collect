package collect.util;

import com.common.collect.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hznijianfeng on 2018/8/14.
 */

@Slf4j
public class DateUtilTest {

    public static void main(String[] args) {
        log.info("now:{}", DateUtil.now());

        log.info("parseDate:{}", DateUtil.parseDate("2018-12-22 22:10:23", "yyyy-MM-dd HH:mm:ss"));
        log.info("format:{}", DateUtil.format(DateUtil.now(), "yyyy-MM-dd HH:mm:ss.SSS"));

        log.info("getStartOfDay:{}", DateUtil.getStartOfDay(DateUtil.now()));
        log.info("getEndOfDay:{}", DateUtil.getEndOfDay(DateUtil.now()));
        log.info("getYear:{}", DateUtil.getYear(DateUtil.now()));
        log.info("getMonthValue:{}", DateUtil.getMonthValue(DateUtil.now()));
        log.info("getDayOfMonth:{}", DateUtil.getDayOfMonth(DateUtil.now()));
        log.info("getDayOfYear:{}", DateUtil.getDayOfYear(DateUtil.now()));
        log.info("getDayOfWeek:{}", DateUtil.getDayOfWeek(DateUtil.now()));

        log.info("plusDays:{}", DateUtil.plusDays(DateUtil.now(), 32));
        log.info("plusMonths:{}", DateUtil.plusMonths(DateUtil.now(), 13));
        log.info("plusYears:{}", DateUtil.plusYears(DateUtil.now(), 3));

        log.info("minusDays:{}", DateUtil.minusDays(DateUtil.now(), 32));
        log.info("minusMonths:{}", DateUtil.minusMonths(DateUtil.now(), 13));
        log.info("minusYears:{}", DateUtil.minusYears(DateUtil.now(), 3));

        log.info("toLocalDate:{}", DateUtil.toLocalDate(DateUtil.now()));
        log.info("toLocalDateTime:{}", DateUtil.toLocalDateTime(DateUtil.now()));
        log.info("localDateToDate:{}", DateUtil.toDate(DateUtil.toLocalDate(DateUtil.now())));
        log.info("localDateTimeToDate:{}", DateUtil.toDate(DateUtil.toLocalDateTime(DateUtil.now())));

    }

}
