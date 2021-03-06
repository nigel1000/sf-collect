package framework.excel.base;

import com.common.collect.framework.excel.annotations.ExcelEntity;
import com.common.collect.framework.excel.annotations.ExcelImport;
import com.common.collect.framework.excel.context.EventModelContext;
import com.common.collect.framework.excel.define.IEventModelParseHandler;
import com.common.collect.lib.util.fastjson.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

@Slf4j
public class DefaultEventModelParseHandler implements IEventModelParseHandler {

    @Override
    public void handle(EventModelContext eventModelContext) {
        log.info("#################################");
        log.info("row size:" + eventModelContext.getRows().size());
        for (List<String> obj : eventModelContext.getRows()) {
            log.info("col size:" + obj.size());
            log.info(JsonUtil.bean2json(obj));
            log.info(JsonUtil.bean2json(toDomain(obj, Domain.class)));
            log.info(JsonUtil.bean2json(toDomain(obj, DomainSortByField.class)));
        }
    }

    @Data
    public static class Domain {

        @ExcelImport(colIndex = "0")
        private String s1;
        @ExcelImport(colIndex = "1")
        private String s2;
        @ExcelImport(colIndex = "2")
        private String s3;
        @ExcelImport(colIndex = "3")
        private String s4;
        @ExcelImport(colIndex = "4")
        private String s5;
        @ExcelImport(colIndex = "5")
        private String s6;
        @ExcelImport(colIndex = "6")
        private String s7;
        @ExcelImport(colIndex = "2:4", isMultiCol = true, dataType = String.class)
        private List<String> s24;

    }

    @Data
    @ExcelEntity(colIndexStrategy = ExcelEntity.ColIndexStrategyEnum.by_field_place_default)
    public static class DomainSortByField {

        @ExcelImport
        private String s5;
        @ExcelImport
        private String s6;
        @ExcelImport
        private String s7;
        @ExcelImport(colIndex = "2:4", isMultiCol = true, dataType = String.class)
        private List<String> s24;
        @ExcelImport
        private String s1;
        @ExcelImport(colIndex = "1")
        private String s2;
        @ExcelImport
        private String s3;
        @ExcelImport
        private String s4;


    }

}
