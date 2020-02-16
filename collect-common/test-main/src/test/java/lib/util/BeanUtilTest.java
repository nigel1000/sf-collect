package lib.util;

import com.common.collect.lib.util.spring.BeanUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nijianfeng on 2019/7/28.
 */

@Slf4j
public class BeanUtilTest {


    @Data
    private static class Domain {

        private String s1;
        private String s2;
        private String s3;
        private String s4;
        private String s5;
        private String s6;
        private String s7;
        private List<String> s24;

        public static Domain genSource() {
            Domain source = new Domain();
            source.setS1("source");
            source.setS2("source");
            source.setS3("source");
            source.setS4("source");
            source.setS5("source");
            return source;
        }

        public static Domain genTarget() {
            Domain source = new Domain();
            source.setS4("target");
            source.setS5("target");
            source.setS6("target");
            source.setS7("target");
            source.setS24(Arrays.asList("target", "target"));
            return source;
        }

    }

    public static void main(String[] args) {

        // 不为空
        String[] notNull = BeanUtil.getPropertyNames(Domain.genSource(), BeanUtil.NeedPropertyType.NOT_NULL);
        log.info("notNull:{}", Arrays.asList(notNull));

        // 为空
        String[] isNull = BeanUtil.getPropertyNames(Domain.genSource(), BeanUtil.NeedPropertyType.NULL);
        log.info("isNull:{}", Arrays.asList(isNull));

        // all
        String[] all = BeanUtil.getPropertyNames(Domain.genSource(), BeanUtil.NeedPropertyType.ALL);
        log.info("all:{}", Arrays.asList(all));

        Domain target = Domain.genTarget();
        log.info("source:{}", Domain.genSource());
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreSourceNullAndTargetNotNullProperty(Domain.genSource(), target);
        log.info("genBeanIgnoreSourceNullAndTargetNotNullProperty:{}", target);

        target = Domain.genTarget();
        log.info("source:{}", Domain.genSource());
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreSourceNullProperty(Domain.genSource(), target);
        log.info("genBeanIgnoreSourceNullProperty:{}", target);

        target = Domain.genTarget();
        log.info("source:{}", Domain.genSource());
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreTargetNotNullProperty(Domain.genSource(), target);
        log.info("genBeanIgnoreTargetNotNullProperty:{}", target);

        target = Domain.genTarget();
        log.info("source:{}", Domain.genSource());
        log.info("target:{}", target);
        BeanUtil.genBeanAllProperty(Domain.genSource(), target);
        log.info("genBeanAllProperty:{}", target);

    }

}
