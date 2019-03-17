package collect.debug;

import collect.debug.mybatis.TestMybatis;
import com.common.collect.debug.mybatis.DBUtil;
import com.common.collect.debug.mybatis.generator.core.DB2Domain;
import com.common.collect.debug.mybatis.generator.core.DB2Mapper;
import com.common.collect.debug.mybatis.generator.domain.param.DomainParam;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.debug.mybatis.generator.domain.param.MapperParam;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class MybatisGenerator {

    public static String path;

    static {
        path = TestMybatis.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "src/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "src/";
        }
    }

    public static void main(String[] args) throws Exception {
        genTest();
        genFlowLog();
    }

    private static void genFlowLog() throws Exception {
        String path = MybatisGenerator.path.replaceAll("collect-debug", "collect-model");

        Properties properties = PropertiesLoaderUtils.loadAllProperties("db.properties");
        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbSchema("test_base_mapper");
        globalParam.setDbUrl(properties.get("url").toString());
        globalParam.setDbUser(properties.get("name").toString());
        globalParam.setDbPwd(properties.get("password").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setPrefixPath(path);
        globalParam.setTableNames(Lists.newArrayList("flow_log"));
        globalParam.validSelf();

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(false);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setMapperPrefixPath(path + "main/resources/mapper/");
        mapperParam.setDaoPackagePath("com.common.collect.model.flowlog.mapper");
        mapperParam.setDomainPackagePath("com.common.collect.model.flowlog");
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();
    }

    private static void genTest() throws Exception {
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
        domainParam.setPrefixPath(path + "test/java/collect/debug/mybatis/domain/");
        domainParam.setPackagePath("collect.debug.mybatis.domain");
        domainParam.validSelf();
        DB2Domain.genDomain(domainParam);

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(true);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setDaoPrefixPath(path + "test/java/collect/debug/mybatis/dao/");
        mapperParam.setMapperPrefixPath(path + "test/resources/mybatis/");
        mapperParam.setDaoPackagePath("collect.debug.mybatis.dao");
        mapperParam.setDomainPackagePath(domainParam.getPackagePath());
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        MapperParam daoParam = new MapperParam(globalParam);
        daoParam.setGenMapper(true);
        daoParam.setGenDao(false);
        daoParam.setDaoSuffixName("dao");
        daoParam.setMapperPrefixPath(path + "test/resources/mybatis/");
        daoParam.setDaoPackagePath("collect.debug.mybatis.dao");
        daoParam.setDomainPackagePath(domainParam.getPackagePath());
        daoParam.validSelf();
        DB2Mapper.genMapper(daoParam);

        DBUtil.releaseResource();
    }

}
