package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.mybatis.generator.DBUtil;
import com.common.collect.container.mybatis.generator.core.DB2Domain;
import com.common.collect.container.mybatis.generator.core.DB2Mapper;
import com.common.collect.container.mybatis.generator.domain.param.DomainParam;
import com.common.collect.container.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.container.mybatis.generator.domain.param.MapperParam;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class MybatisGenerator {

    private static String path;
    private static Properties properties;

    static {
        path = MybatisGenerator.class.getResource("/").getPath();
        if (path.contains(":/")) {
            path = path.substring(1, path.indexOf("target")) + "logs/";
        } else {
            path = path.substring(0, path.indexOf("target")) + "logs/";
        }
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("db.properties");
        } catch (Exception ex) {
            throw UnifiedException.gen("属性读取失败");
        }
    }

    public static void main(String[] args) {
        genTest();
        genFlowLog();
    }

    private static void genFlowLog() {

        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbUrl(properties.get("jdbc.mysql.url").toString());
        globalParam.setDbUser(properties.get("jdbc.mysql.name").toString());
        globalParam.setDbPwd(properties.get("jdbc.mysql.password").toString());
        globalParam.setDbDriver(properties.get("jdbc.mysql.driver").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setPrefixPath(path);
        globalParam.setTableNames(Lists.newArrayList("flow_log"));
        globalParam.validSelf();

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(false);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setMapperPrefixPath(path);
        mapperParam.setDaoPackagePath("com.common.collect.model.flowlog.mapper");
        mapperParam.setDomainPackagePath("com.common.collect.model.flowlog");
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();
    }

    private static void genTest() {

        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbUrl(properties.get("jdbc.mysql.url").toString());
        globalParam.setDbUser(properties.get("jdbc.mysql.name").toString());
        globalParam.setDbPwd(properties.get("jdbc.mysql.password").toString());
        globalParam.setDbDriver(properties.get("jdbc.mysql.driver").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setPrefixPath(path);
        globalParam.setTableNames(Lists.newArrayList("test"));
        globalParam.validSelf();

        DomainParam domainParam = new DomainParam(globalParam);
        domainParam.setPrefixPath(path);
        domainParam.setPackagePath("collect.debug.mybatis.domain");
        domainParam.validSelf();
        DB2Domain.genDomain(domainParam);

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(true);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setDaoPrefixPath(path);
        mapperParam.setMapperPrefixPath(path);
        mapperParam.setDaoPackagePath("collect.debug.mybatis.dao");
        mapperParam.setDomainPackagePath(domainParam.getPackagePath());
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();
    }

}
