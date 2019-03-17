package com.common.collect.debug.mybatis.generator.core;

import com.common.collect.debug.mybatis.DBUtil;
import com.common.collect.debug.mybatis.TemplateUtil;
import com.common.collect.debug.mybatis.generator.domain.db.Field;
import com.common.collect.debug.mybatis.generator.domain.db.Table;
import com.common.collect.debug.mybatis.generator.domain.param.GlobalParam;
import com.common.collect.debug.mybatis.generator.domain.param.MapperParam;
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
public class DB2Mapper {

    public static void genMapper(MapperParam mapperParam) {

        GlobalParam globalParam = mapperParam.getGlobalParam();
        Map<String, Table> tableMap = DBUtil.getTables(globalParam, mapperParam.getTableNames());
        for (Map.Entry<String, Table> tableEntry : tableMap.entrySet()) {
            Table table = tableEntry.getValue();
            String tableName = tableEntry.getKey();
            String className = ConvertUtil.firstUpper(ConvertUtil.underline2Camel(tableName));
            String daoSuffixName = ConvertUtil.firstUpper(mapperParam.getDaoSuffixName());
            Map<String, Object> tplMap = Maps.newHashMap();
            tplMap.put("author", globalParam.getAuthor());
            tplMap.put("date", globalParam.getDate());
            tplMap.put("daoPackagePath", mapperParam.getDaoPackagePath());
            tplMap.put("domainPackagePath", mapperParam.getDomainPackagePath());
            tplMap.put("daoSuffixName", daoSuffixName);
            tplMap.put("tableName", tableName);
            tplMap.put("className", className);
            tplMap.put("sqlIds", mapperParam.getSqlIds());
            tplMap.put("insertDate2Now", mapperParam.getInsertDate2Now());
            List<Map<String, Object>> fieldInfos = Lists.newArrayList();
            for (Field field : table.getFields()) {
                Map<String, Object> fieldInfoMap = Maps.newHashMap();
                fieldInfoMap.put("type", DBUtil.getJavaTypeBySqlType(field.getType()));
                fieldInfoMap.put("dbName", field.getField());
                fieldInfoMap.put("javaName", ConvertUtil.firstLower(ConvertUtil.underline2Camel(field.getField())));
                fieldInfos.add(fieldInfoMap);
            }
            tplMap.put("fieldInfos", fieldInfos);

            String fileName = className + daoSuffixName + ".xml";
            String dirPath = mapperParam.getMapperPrefixPath();
            String filePath = dirPath + fileName;
            String fileContent = TemplateUtil.genTemplate("/tpl", "mapper.tpl", tplMap);
            if (mapperParam.isGenMapper()) {
                log.info("DB2Mapper mapper:filePath:{},args:{},tplMap:{}", filePath, mapperParam, tplMap);
                TemplateUtil.genFile(dirPath, fileName, fileContent);
            }

            fileName = className + daoSuffixName + ".java";
            dirPath = mapperParam.getDaoPrefixPath();
            filePath = dirPath + fileName;
            fileContent = TemplateUtil.genTemplate("/tpl", "dao.tpl", tplMap);
            if (mapperParam.isGenDao()) {
                log.info("DB2Mapper dao:filePath:{},args:{},tplMap:{}", filePath, mapperParam, tplMap);
                TemplateUtil.genFile(dirPath, fileName, fileContent);
            }
        }
    }

}
