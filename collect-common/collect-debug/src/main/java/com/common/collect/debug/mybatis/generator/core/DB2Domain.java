package com.common.collect.debug.mybatis.generator.core;

import com.common.collect.debug.mybatis.DBUtil;
import com.common.collect.debug.mybatis.TemplateUtil;
import com.common.collect.debug.mybatis.generator.domain.db.Field;
import com.common.collect.debug.mybatis.generator.domain.db.Table;
import com.common.collect.debug.mybatis.generator.domain.param.DomainParam;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.util.ConvertUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Slf4j
public class DB2Domain {

    public static void genDomain(DomainParam domainParam) {

        GlobalParam globalParam = domainParam.getGlobalParam();
        Map<String, Table> tableMap = DBUtil.getTables(globalParam, domainParam.getTableNames());
        for (Map.Entry<String, Table> tableEntry : tableMap.entrySet()) {
            Table table = tableEntry.getValue();
            String tableName = tableEntry.getKey();
            String className = ConvertUtil.firstUpper(ConvertUtil.underline2Camel(tableName));
            Map<String, Object> tplMap = Maps.newHashMap();
            tplMap.put("author", globalParam.getAuthor());
            tplMap.put("date", globalParam.getDate());
            tplMap.put("package", domainParam.getPackagePath());
            tplMap.put("className", className);
            tplMap.put("tableComment", table.getComment());
            List<Map<String, Object>> fieldInfos = Lists.newArrayList();
            for (Field field : table.getFields()) {
                Map<String, Object> fieldInfoMap = Maps.newHashMap();
                fieldInfoMap.put("type", DBUtil.getJavaTypeBySqlType(field.getType()));
                fieldInfoMap.put("name", ConvertUtil.firstLower(ConvertUtil.underline2Camel(field.getField())));
                fieldInfoMap.put("memo", field.getMemo());
                fieldInfos.add(fieldInfoMap);
            }
            tplMap.put("fieldInfos", fieldInfos);

            String fileName = className + ".java";
            String dirPath = domainParam.getPrefixPath();
            String filePath = dirPath + fileName;
            String fileContent = TemplateUtil.genTemplate("/tpl", "domain.tpl", tplMap);
            log.info("DB2Domain:filePath:{},args:{},tplMap:{}", filePath, domainParam, tplMap);
            TemplateUtil.genFile(dirPath, fileName, fileContent);
        }
    }

}
