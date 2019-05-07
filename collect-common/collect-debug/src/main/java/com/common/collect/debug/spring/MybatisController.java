package com.common.collect.debug.spring;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.debug.mybatis.DBUtil;
import com.common.collect.debug.mybatis.generator.core.DB2Domain;
import com.common.collect.debug.mybatis.generator.core.DB2Mapper;
import com.common.collect.debug.mybatis.generator.domain.param.DomainParam;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.debug.mybatis.generator.domain.param.MapperParam;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.FileUtil;
import com.common.collect.util.SplitUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/4/11.
 */

@RestController
@RequestMapping("/back/door/mybatis")
public class MybatisController {

    @Value("${jdbc.mysql.url}")
    private String url;
    @Value("${jdbc.mysql.driver}")
    private String driver;
    @Value("${jdbc.mysql.name}")
    private String name;
    @Value("${jdbc.mysql.password}")
    private String password;

    private String mybatis_key = "c26b094cc27346379266147682c41fc0";

    @RequestMapping("/generator")
    public void generator(@RequestParam("tableName") String tableName,
                          @RequestParam(value = "author", required = false) String author,
                          @RequestParam(value = "schema", required = false) String schema,
                          @RequestParam(value = "remove", required = false) String remove,
                          @RequestParam(value = "type", required = false) String type,
                          @RequestParam(value = "uuid") String uuid,
                          HttpServletResponse response) {

        if (!uuid.equals(mybatis_key)) {
            throw UnifiedException.gen("无权限访问此链接");
        }

        if (EmptyUtil.isEmpty(type)) {
            type = "all";
        }
        if (EmptyUtil.isEmpty(author)) {
            author = "hznijianfeng";
        }

        List<File> files = Lists.newArrayList();

        GlobalParam globalParam = new GlobalParam();
        if (EmptyUtil.isNotEmpty(schema)) {
            globalParam.setDbSchema(schema);
        }
        globalParam.setDbUrl(url);
        globalParam.setDbUser(name);
        globalParam.setDbPwd(password);
        globalParam.setDbDriver(driver);
        globalParam.setAuthor(author);
        globalParam.setTableNames(SplitUtil.split2StringByComma(tableName));
        globalParam.validSelf();

        String domainPackagePath = "com.common.collect.web.meta";
        String mapperPackagePath = "com.common.collect.web.meta.mapper";
        String tmpPath = System.getProperty("java.io.tmpdir") + "/generator/";

        if (type.equals("all") || type.equals("domain")) {
            DomainParam domainParam = new DomainParam(globalParam);
            domainParam.setPrefixPath(tmpPath);
            domainParam.setPackagePath(domainPackagePath);
            domainParam.validSelf();
            files.addAll(DB2Domain.genDomain(domainParam));
        }

        if (type.equals("all") || type.equals("mapper")) {
            MapperParam mapperParam = new MapperParam(globalParam);
            mapperParam.setGenMapper(true);
            mapperParam.setGenDao(true);
            mapperParam.setDaoSuffixName("mapper");
            mapperParam.setDaoPrefixPath(tmpPath);
            mapperParam.setMapperPrefixPath(tmpPath);
            mapperParam.setDaoPackagePath(mapperPackagePath);
            mapperParam.setDomainPackagePath(domainPackagePath);
            mapperParam.setSqlIds(Lists.newArrayList("create", "creates", "update"));
            mapperParam.setInsertDate2Now(Lists.newArrayList("createdAt", "updatedAt", "createAt", "updateAt",
                    "dbCreateTime", "dbUpdateTime"));
            mapperParam.validSelf();
            files.addAll(DB2Mapper.genMapper(mapperParam));
        }

        DBUtil.releaseResource();

        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write("<html><body>");
            for (File file : files) {
                out.write("<h2><font color=\"red\">");
                String fileName = file.getName();
                String fileContent = FileUtil.getString(file);
                if (EmptyUtil.isNotEmpty(remove)) {
                    fileName = fileName.replaceAll(remove, "");
                    fileContent = fileContent.replaceAll(remove, "");
                }
                out.write(fileName);
                out.write("</font></h2>");
                out.write("<pre>");
                out.write(StringEscapeUtils.escapeXml11(fileContent));
                out.write("</pre>");
            }
            out.write("</body></html>");
            out.flush();
        } catch (Exception ex) {
            throw UnifiedException.gen("输出内容出错", ex);
        }
    }


}