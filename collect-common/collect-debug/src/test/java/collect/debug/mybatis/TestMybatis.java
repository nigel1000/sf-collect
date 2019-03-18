package collect.debug.mybatis;

import collect.debug.mybatis.dao.TestDao;
import collect.debug.mybatis.dao.TestMapper;
import collect.debug.mybatis.domain.Test;
import com.common.collect.container.BeanUtil;
import com.common.collect.container.TransactionHelper;
import com.common.collect.container.mybatis.MybatisContext;
import com.common.collect.util.IdUtil;
import com.common.collect.util.log4j.Slf4jUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Slf4j
public class TestMybatis {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        TestMapper testMapper = (TestMapper) applicationContext.getBean("testMapper");

        Test test = new Test();
        test.setBigintType(1L);
        test.setBitType(Boolean.FALSE);
        test.setCharType("char");
        test.setDatetimeType(new Date());
        test.setDateType(new Date());
        test.setDecimalType(new BigDecimal(1));
        test.setDoubleType(new BigDecimal("11.11"));
        test.setIntType(3);
        test.setMediumintType(23L);
        test.setMediumtextType("你好");
        test.setSmallintType(23);
        test.setStringType("测试");
        test.setTinyintType(3);

        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");
        transactionHelper.aroundBiz(() -> {
            log.info("################################ testMapper #################################");
            log.info("create -> return:{},id:{}", testMapper.create(test), test.getId());
            log.info("load -> return:{},id:{}", testMapper.load(test.getId()), test.getId());
            test.setStringType("测试 更新 testMapper");
            log.info("update -> return:{},id:{}", testMapper.update(test), test.getId());
            log.info("load -> return:{},id:{}", testMapper.load(test.getId()), test.getId());
            log.info("delete -> return:{},id:{}", testMapper.delete(test.getId()), test.getId());
            log.info("load -> return:{},id:{}", testMapper.load(test.getId()), test.getId());

            Long id1 = IdUtil.snowflakeId();
            Long id2 = IdUtil.snowflakeId();
            Test test1 = BeanUtil.genBean(test, Test.class);
            test1.setId(id1);
            Test test2 = BeanUtil.genBean(test, Test.class);
            test2.setId(id2);

            log.info("create -> return:{}", testMapper.creates(Lists.newArrayList(test1, test2)));
            log.info("loads -> return:{}", testMapper.loads(Lists.newArrayList(id1, id2)));
            log.info("deletes -> return:{}", testMapper.deletes(Lists.newArrayList(id1, id2)));
            log.info("loads -> return:{}", testMapper.loads(Lists.newArrayList(id1, id2)));

        });

        TestDao testDao = (TestDao) applicationContext.getBean("testDao");
        transactionHelper.aroundBiz(() -> {
            log.info("################################ testDao #################################");
            log.info("create -> return:{},id:{}", testDao.create(test), test.getId());
            log.info("load -> return:{},id:{}", testDao.load(test.getId()), test.getId());
            test.setStringType("测试 更新 testDao");
            log.info("update -> return:{},id:{}", testDao.update(test), test.getId());
            log.info("load -> return:{},id:{}", testDao.load(test.getId()), test.getId());
            log.info("delete -> return:{},id:{}", testDao.delete(test.getId()), test.getId());
            log.info("load -> return:{},id:{}", testDao.load(test.getId()), test.getId());

            Long id1 = IdUtil.snowflakeId();
            Long id2 = IdUtil.snowflakeId();
            Test test1 = BeanUtil.genBean(test, Test.class);
            test1.setId(id1);
            Test test2 = BeanUtil.genBean(test, Test.class);
            test2.setId(id2);

            log.info("create -> return:{}", testDao.creates(Lists.newArrayList(test1, test2)));
            log.info("loads -> return:{}", testDao.loads(Lists.newArrayList(id1, id2)));
            log.info("deletes -> return:{}", testDao.deletes(Lists.newArrayList(id1, id2)));
            log.info("loads -> return:{}", testDao.loads(Lists.newArrayList(id1, id2)));

        });

        Slf4jUtil.setLogLevel("debug");
        // 获取执行 sql
        MybatisContext.setEnableSqlRecord(true);
        testDao.load(IdUtil.snowflakeId());
        String sql = MybatisContext.getSqlRecord(true);
        log.info("execute sql -> return:{}", sql);
        MybatisContext.addLogFilterKey("test", null);
        testDao.load(IdUtil.snowflakeId());
    }

}
