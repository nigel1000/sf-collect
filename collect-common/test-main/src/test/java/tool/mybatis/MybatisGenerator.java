package tool.mybatis;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.tool.mybatis.generator.DBUtil;
import com.common.collect.tool.mybatis.generator.core.DB2Domain;
import com.common.collect.tool.mybatis.generator.core.DB2Mapper;
import com.common.collect.tool.mybatis.generator.domain.param.DomainParam;
import com.common.collect.tool.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.tool.mybatis.generator.domain.param.MapperParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class MybatisGenerator {

    private static String root = Paths.get(MybatisGenerator.class.getResource("/").getPath()).getParent().getParent().toString() + "/";
    private static Properties properties;

    static {
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("db.properties");
        } catch (Exception ex) {
            throw UnifiedException.gen("属性读取失败");
        }
    }

    public static void main(String[] args) {
        root = Paths.get(root).getParent().toString();
        log.info("root:\t" + root);
        genFlowLog();
        genTaskRecord();
    }

    private static void genFlowLog() {

        String path = root + "/model-flowlog/src/main/";

        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbUrl(properties.get("jdbc.mysql.url").toString());
        globalParam.setDbUser(properties.get("jdbc.mysql.name").toString());
        globalParam.setDbPwd(properties.get("jdbc.mysql.password").toString());
        globalParam.setDbDriver(properties.get("jdbc.mysql.driver").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setTableNames(Arrays.asList("flow_log"));
        globalParam.validSelf();

        DomainParam domainParam = new DomainParam(globalParam);
        domainParam.setPrefixPath(path + "java/com/common/collect/model/flowlog/infrastructure/");
        domainParam.setPackagePath("com.common.collect.model.flowlog.infrastructure");
        domainParam.validSelf();
        DB2Domain.genDomain(domainParam);

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(true);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setMapperPrefixPath(path + "resources/mybatis/");
        mapperParam.setDaoPrefixPath(path + "java/com/common/collect/model/flowlog/infrastructure/");
        mapperParam.setDaoPackagePath("com.common.collect.model.flowlog.infrastructure");
        mapperParam.setDomainPackagePath("com.common.collect.model.flowlog.infrastructure");
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();
    }

    private static void genTaskRecord() {

        String path = root + "/model-taskrecord/src/main/";

        GlobalParam globalParam = new GlobalParam();
        globalParam.setDbUrl(properties.get("jdbc.mysql.url").toString());
        globalParam.setDbUser(properties.get("jdbc.mysql.name").toString());
        globalParam.setDbPwd(properties.get("jdbc.mysql.password").toString());
        globalParam.setDbDriver(properties.get("jdbc.mysql.driver").toString());
        globalParam.setAuthor("hznijianfeng");
        globalParam.setTableNames(Arrays.asList("task_record"));
        globalParam.validSelf();

        DomainParam domainParam = new DomainParam(globalParam);
        domainParam.setPrefixPath(path + "java/com/common/collect/model/taskrecord/infrastructure/");
        domainParam.setPackagePath("com.common.collect.model.taskrecord.infrastructure");
        domainParam.validSelf();
        DB2Domain.genDomain(domainParam);

        MapperParam mapperParam = new MapperParam(globalParam);
        mapperParam.setGenMapper(true);
        mapperParam.setGenDao(true);
        mapperParam.setDaoSuffixName("mapper");
        mapperParam.setMapperPrefixPath(path + "resources/mybatis/");
        mapperParam.setDaoPrefixPath(path + "java/com/common/collect/model/taskrecord/infrastructure/");
        mapperParam.setDaoPackagePath("com.common.collect.model.taskrecord.infrastructure");
        mapperParam.setDomainPackagePath("com.common.collect.model.taskrecord.infrastructure");
        mapperParam.validSelf();
        DB2Mapper.genMapper(mapperParam);

        DBUtil.releaseResource();
    }

}
