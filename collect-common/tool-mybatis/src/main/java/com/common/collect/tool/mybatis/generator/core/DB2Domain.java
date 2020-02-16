package com.common.collect.tool.mybatis.generator.core;

import com.common.collect.lib.util.ConvertUtil;
import com.common.collect.lib.util.FileUtil;
import com.common.collect.lib.util.freemarker.TemplateUtil;
import com.common.collect.tool.mybatis.generator.DBUtil;
import com.common.collect.tool.mybatis.generator.domain.db.Field;
import com.common.collect.tool.mybatis.generator.domain.db.Table;
import com.common.collect.tool.mybatis.generator.domain.param.DomainParam;
import com.common.collect.tool.mybatis.generator.domain.param.GlobalParam;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Slf4j
public class DB2Domain {

    public static List<File> genDomain(DomainParam domainParam) {
        List<File> files = new ArrayList<>();

        GlobalParam globalParam = domainParam.getGlobalParam();
        Map<String, Table> tableMap = DBUtil.getTables(globalParam, domainParam.getTableNames());
        for (Map.Entry<String, Table> tableEntry : tableMap.entrySet()) {
            Table table = tableEntry.getValue();
            String tableName = tableEntry.getKey();
            String className = ConvertUtil.firstUpper(ConvertUtil.underline2Camel(tableName));
            Map<String, Object> tplMap = new HashMap<>();
            tplMap.put("author", globalParam.getAuthor());
            tplMap.put("date", globalParam.getDate());
            tplMap.put("package", domainParam.getPackagePath());
            tplMap.put("className", className);
            tplMap.put("tableComment", table.getComment());
            List<Map<String, Object>> fieldInfos = new ArrayList<>();
            for (Field field : table.getFields()) {
                Map<String, Object> fieldInfoMap = new HashMap<>();
                fieldInfoMap.put("type", DBUtil.getJavaTypeBySqlType(field.getType()));
                fieldInfoMap.put("name", ConvertUtil.firstLower(ConvertUtil.underline2Camel(field.getField())));
                fieldInfoMap.put("memo", field.getMemo());
                fieldInfos.add(fieldInfoMap);
            }
            tplMap.put("fieldInfos", fieldInfos);

            String fileName = className + ".java";
            String dirPath = domainParam.getPrefixPath();
            String filePath = dirPath + fileName;
            String fileContent = TemplateUtil.getStringByTemplate("/tpl/mybatis", "domain.tpl", tplMap);
            log.info("DB2Domain:filePath:{},args:{},tplMap:{}", filePath, domainParam, tplMap);
            FileUtil.createFile(filePath, false, fileContent.getBytes(), true);
            files.add(new File(filePath));
        }
        return files;
    }

}
