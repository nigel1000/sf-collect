package collect.debug.mybatis;

import collect.debug.mybatis.test.dao.TestMapper;
import collect.debug.mybatis.test.domain.Test;
import com.common.collect.container.BeanUtil;
import com.common.collect.container.TransactionHelper;
import com.common.collect.debug.mybatis.DBUtil;
import com.common.collect.debug.mybatis.generator.core.DB2Domain;
import com.common.collect.debug.mybatis.generator.core.DB2Mapper;
import com.common.collect.debug.mybatis.generator.domain.param.DomainParam;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.debug.mybatis.generator.domain.param.MapperParam;
import com.common.collect.util.IdUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Slf4j
public class MybatisTest {

    public static String path;

    static {
        path = MybatisTest.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/test/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/test/";
        }
    }

    public static void main(String[] args) throws Exception {
//        generatorFile();
        validFile();
    }

    private static void validFile() {
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
            log.info("create -> return:{},id:{}", testMapper.create(test), test.getId());
            log.info("load -> return:{},id:{}", testMapper.load(test.getId()), test.getId());
            test.setStringType("测试 更新");
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

    }

    private static void generatorFile() throws Exception {
        Properties properties = PropertiesLoaderUtils.loadAllProperties("db.properties");

        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbSchema("test_base_mapper");
        globalParam.setDbUrl(properties.get("url").toString());
        globalParam.setDbUser(properties.get("name").toString());
        globalParam.setDbPwd(properties.get("password").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setPrefixPath(path);
        globalParam.setTableNames(Lists.newArrayList("test"));
        globalParam.validSelf();

        DomainParam domainParam = new DomainParam(globalParam);
        domainParam.setPrefixPath(path + "java/collect/debug/mybatis/test/domain/");
        domainParam.setPackagePath("collect.debug.mybatis.test.domain");
        domainParam.validSelf();
        DB2Domain.genDomain(domainParam);

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setDaoPrefixPath(path + "java/collect/debug/mybatis/test/dao/");
        mapperParam.setMapperPrefixPath(path + "resources/mybatis/");
        mapperParam.setDaoPackagePath("collect.debug.mybatis.test.dao");
        mapperParam.setDomainPackagePath(domainParam.getPackagePath());
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();

    }

}
