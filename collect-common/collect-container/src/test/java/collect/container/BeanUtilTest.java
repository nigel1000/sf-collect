package collect.container;

import collect.container.excel.base.Domain;
import com.common.collect.container.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Created by nijianfeng on 2019/7/28.
 */

@Slf4j
public class BeanUtilTest {

    public static void main(String[] args) {

        Domain source = new Domain();
        source.setS1("www.baidu.com");
        source.setS7("9010");

        Domain target = new Domain();
        target.setS1("www.xxx.com");
        target.setS3("s3");

        // 不为空
        String[] notNull = BeanUtil.getPropertyNames(source, BeanUtil.NeedPropertyType.NOT_NULL);
        log.info("notNull:{}", Arrays.asList(notNull));

        // 为空
        String[] isNull = BeanUtil.getPropertyNames(source, BeanUtil.NeedPropertyType.NULL);
        log.info("isNull:{}", Arrays.asList(isNull));

        // all
        String[] all = BeanUtil.getPropertyNames(source, BeanUtil.NeedPropertyType.ALL);
        log.info("all:{}", Arrays.asList(all));

        log.info("source:{}", source);
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreSourceNullAndTargetNotNullProperty(source, target);
        log.info("genBeanIgnoreSourceNullAndTargetNotNullProperty:{}", target);

        target = new Domain();
        target.setS1("www.xxx.com");
        target.setS3("s3");

        log.info("source:{}", source);
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreSourceNullProperty(source, target);
        log.info("genBeanIgnoreSourceNullProperty:{}", target);


        target = new Domain();
        target.setS1("www.xxx.com");
        target.setS3("s3");

        log.info("source:{}", source);
        log.info("target:{}", target);
        BeanUtil.genBeanIgnoreTargetNotNullProperty(source, target);
        log.info("genBeanIgnoreTargetNotNullProperty:{}", target);

    }

}
