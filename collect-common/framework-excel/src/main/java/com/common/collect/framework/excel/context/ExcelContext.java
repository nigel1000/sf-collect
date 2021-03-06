package com.common.collect.framework.excel.context;

import com.common.collect.framework.excel.annotations.*;
import com.common.collect.framework.excel.base.ExcelConstants;
import com.common.collect.framework.excel.define.*;
import com.common.collect.framework.excel.define.bean.SingletonBeanFactory;
import com.common.collect.framework.excel.define.check.MaxCheckImportHandler;
import com.common.collect.framework.excel.define.check.RegexCheckImportHandler;
import com.common.collect.framework.excel.define.check.RequireCheckImportHandler;
import com.common.collect.framework.excel.define.convert.ByTypeConvertExportHandler;
import com.common.collect.framework.excel.define.convert.ByTypeConvertImportHandler;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.*;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/6/15.
 */

@Getter
public class ExcelContext {

    private static Map<Class, ExcelContext> cacheClassParseResult = new LinkedHashMap<>();
    // class
    private Class<?> clazz;
    // ExcelEntity
    private ExcelEntity excelEntity;
    private ExcelEntity.ColIndexStrategyEnum colIndexStrategy;
    private Class<? extends ICellConfig> cellConfigCls;
    private ICellConfig cellConfig;
    private Class<? extends IBeanFactory> beanFactoryCls;
    private IBeanFactory beanFactory;
    // field
    private Map<String, Field> fieldMap = new LinkedHashMap<>();
    private Map<String, Class> fieldClsMap = new LinkedHashMap<>();
    private Map<String, Method> fieldGetMethodMap = new LinkedHashMap<>();
    private Map<String, Method> fieldSetMethodMap = new LinkedHashMap<>();
    private List<String> fieldNameList = new ArrayList<>();
    // ExcelImport
    private Map<String, ExcelImport> excelImportMap = new LinkedHashMap<>();
    private Map<String, String> excelImportColIndexMap = new LinkedHashMap<>();
    private Map<String, List<Integer>> excelImportColIndexNumMap = new LinkedHashMap<>();
    private Map<String, Boolean> excelImportIsMultiColMap = new LinkedHashMap<>();
    private Map<String, Class> excelImportMultiColListTypeMap = new LinkedHashMap<>();
    private Map<String, Class<? extends IColIndexParser>> excelImportColIndexParserClsMap = new LinkedHashMap<>();
    private Map<String, IColIndexParser> excelImportColIndexParserMap = new LinkedHashMap<>();
    private Map<String, String> excelImportTitleMap = new LinkedHashMap<>();
    // ExcelExport
    private Map<String, ExcelExport> excelExportMap = new LinkedHashMap<>();
    private Map<String, Integer> excelExportColIndexMap = new LinkedHashMap<>();
    private Map<String, String> excelExportTitleMap = new LinkedHashMap<>();
    private Map<String, Class<? extends ICellConfig>> excelExportCellConfigClsMap = new LinkedHashMap<>();
    private Map<String, ICellConfig> excelExportCellConfigMap = new LinkedHashMap<>();
    // ExcelCheck
    private Map<String, ExcelCheck> excelCheckMap = new LinkedHashMap<>();
    private Map<String, Boolean> excelCheckRequiredMap = new LinkedHashMap<>();
    private Map<String, String> excelCheckRequiredTipsMap = new LinkedHashMap<>();
    private Map<String, Long> excelCheckMaxMap = new LinkedHashMap<>();
    private Map<String, String> excelCheckMaxTipsMap = new LinkedHashMap<>();
    private Map<String, String> excelCheckRegexMap = new LinkedHashMap<>();
    private Map<String, String> excelCheckRegexTipsMap = new LinkedHashMap<>();
    private Map<String, List<ICheckImportHandler>> excelCheckImportHandlerMap = new LinkedHashMap<>();
    // ExcelConvert
    private Map<String, ExcelConvert> excelConvertMap = new LinkedHashMap<>();
    // 导入使用
    private Map<String, String> excelConvertDateParseMap = new LinkedHashMap<>();
    private Map<String, String> excelConvertDateParseTipsMap = new LinkedHashMap<>();
    private Map<String, List<IConvertImportHandler>> excelConvertImportHandlerMap = new LinkedHashMap<>();
    // 导出使用
    private Map<String, String> excelConvertDateFormatMap = new LinkedHashMap<>();
    private Map<String, List<IConvertExportHandler>> excelConvertExportHandlerMap = new LinkedHashMap<>();

    private ExcelContext(Class<?> clazz) {
        if (clazz == null) {
            throw UnifiedException.gen("参数不能为空");
        }
        // 处理 class
        this.clazz = clazz;
        // 处理 field
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            ExcelImport excelImport = field.getAnnotation(ExcelImport.class);
            ExcelExport excelExport = field.getAnnotation(ExcelExport.class);
            if (excelImport == null && excelExport == null) {
                continue;
            }
            String fieldName = field.getName();
            Class fieldType = field.getType();
            String setMethodName = "set" + ConvertUtil.firstUpper(fieldName);
            String getMethodName = "get" + ConvertUtil.firstUpper(fieldName);

            excelImportMap.put(fieldName, excelImport);
            excelExportMap.put(fieldName, excelExport);

            fieldNameList.add(fieldName);
            fieldMap.put(fieldName, field);
            fieldClsMap.put(fieldName, fieldType);
            try {
                fieldGetMethodMap.put(fieldName, clazz.getDeclaredMethod(getMethodName));
                fieldSetMethodMap.put(fieldName, clazz.getDeclaredMethod(setMethodName, fieldType));
            } catch (NoSuchMethodException ex) {
                throw UnifiedException.gen(ExcelConstants.MODULE, "解析" + clazz.getName() + "出错", ex);
            }
        }
        init();
    }

    public static ExcelContext excelContext(Class clazz) {
        return cacheClassParseResult.computeIfAbsent(clazz, (cls) -> new ExcelContext(clazz));
    }

    private void init() {
        // 处理 类上的注解
        parseCls();
        // 处理 属性上的注解
        parseField();
    }

    private void parseField() {
        // 处理 导入参数
        int fieldImportIndex = 0;
        int fieldExportIndex = 0;
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String fieldName = entry.getKey();
            Field field = entry.getValue();
            if (isImport(fieldName)) {
                ExcelImport excelImport = excelImportMap.get(fieldName);
                String colIndex = excelImport.colIndex();
                excelImportColIndexMap.put(fieldName, colIndex);
                Class<? extends IColIndexParser> colIndexParserCls = excelImport.colIndexParser();
                excelImportColIndexParserClsMap.put(fieldName, colIndexParserCls);
                IColIndexParser colIndexParser = beanFactory.getBean(colIndexParserCls);
                if (colIndexParser == null) {
                    throw UnifiedException.gen(ExcelConstants.MODULE,
                            StringUtil.format("class:{} fieldName:{} 列下标解析器不能为空", clazz.getName(), fieldName));
                }
                excelImportColIndexParserMap.put(fieldName, colIndexParser);
                List<Integer> colIndexNums = new ArrayList<>();
                switch (colIndexStrategy) {
                    case by_field_config:
                        colIndexNums = CollectionUtil.distinct(colIndexParser.parseColIndex(colIndex));
                        break;
                    case by_field_place:
                        colIndexNums.add(fieldImportIndex++);
                        break;
                    case by_field_place_default:
                        if (EmptyUtil.isNotBlank(colIndex)) {
                            colIndexNums = CollectionUtil.distinct(colIndexParser.parseColIndex(colIndex));
                        } else {
                            colIndexNums.add(fieldImportIndex++);
                        }
                        break;
                }
                if (EmptyUtil.isEmpty(colIndexNums)) {
                    throw UnifiedException.gen(ExcelConstants.MODULE,
                            StringUtil.format("class:{} fieldName:{} colIndex 不能为空", clazz.getName(), fieldName));
                }
                excelImportColIndexNumMap.put(fieldName, colIndexNums);
                // 是 list 就是多列
                boolean isMultiCol = excelImport.isMultiCol();
                excelImportIsMultiColMap.put(fieldName, isMultiCol);
                if (isMultiCol) {
                    if (!fieldClsMap.get(fieldName).equals(List.class)) {
                        throw UnifiedException.gen(ExcelConstants.MODULE,
                                StringUtil.format("class:{} fieldName:{} 多列 返回类型必须是 List", clazz.getName(), fieldName));
                    }
                    excelImportMultiColListTypeMap.put(fieldName, excelImport.dataType());
                } else {
                    if (colIndexNums.size() > 1) {
                        throw UnifiedException.gen(ExcelConstants.MODULE,
                                StringUtil.format("class:{} fieldName:{} 单列 当前指定的 colIndex 为多列", clazz.getName(), fieldName));
                    }
                    excelImportMultiColListTypeMap.put(fieldName, fieldClsMap.get(fieldName));
                }
                excelImportTitleMap.put(fieldName, excelImport.title());
            }

            if (isExport(fieldName)) {
                ExcelExport excelExport = excelExportMap.get(fieldName);
                int colIndex = ExcelConstants.EXCEL_EXPORT_COL_INDEX_DEFAULT;
                switch (colIndexStrategy) {
                    case by_field_config:
                        colIndex = excelExport.colIndex();
                        break;
                    case by_field_place:
                        colIndex = fieldExportIndex++;
                        break;
                    case by_field_place_default:
                        if (excelExport.colIndex() != ExcelConstants.EXCEL_EXPORT_COL_INDEX_DEFAULT) {
                            colIndex = excelExport.colIndex();
                        } else {
                            colIndex = fieldExportIndex++;
                        }
                        break;
                }

                if (colIndex < 0) {
                    throw UnifiedException.gen(ExcelConstants.MODULE,
                            StringUtil.format("class:{} fieldName:{} 导出 colIndex 不能小于0", clazz.getName(), fieldName));
                }
                excelExportColIndexMap.put(fieldName, colIndex);
                excelExportTitleMap.put(fieldName, excelExport.title());
                Class<? extends ICellConfig> cellConfigCls = excelExport.cellConfig();
                excelExportCellConfigClsMap.put(fieldName, cellConfigCls);
                ICellConfig excelExportCellConfigCls = beanFactory.getBean(cellConfigCls);
                ICellConfig cellConfig = (excelExportCellConfigCls != null ? excelExportCellConfigCls : this.cellConfig);
                excelExportCellConfigMap.put(fieldName, cellConfig);
            }

            ExcelCheck excelCheck = field.getAnnotation(ExcelCheck.class);
            if (excelCheck != null) {
                excelCheckMap.put(fieldName, excelCheck);
                boolean required = excelCheck.required();
                excelCheckRequiredMap.put(fieldName, required);
                excelCheckRequiredTipsMap.put(fieldName, excelCheck.requiredTips());
                long max = excelCheck.max();
                excelCheckMaxMap.put(fieldName, max);
                excelCheckMaxTipsMap.put(fieldName, excelCheck.maxTips());
                String regex = excelCheck.regex();
                excelCheckRegexMap.put(fieldName, regex);
                excelCheckRegexTipsMap.put(fieldName, excelCheck.regexTips());
                Class<? extends ICheckImportHandler>[] handlerCls = excelCheck.checkImportHandlers();
                List<ICheckImportHandler> checkHandlers = new ArrayList<>();
                for (Class<? extends ICheckImportHandler> handler : handlerCls) {
                    checkHandlers.add(beanFactory.getBean(handler));
                }
                if (required) {
                    checkHandlers.add(beanFactory.getBean(RequireCheckImportHandler.class));
                }
                if (max != Long.MIN_VALUE) {
                    checkHandlers.add(beanFactory.getBean(MaxCheckImportHandler.class));
                }
                if (EmptyUtil.isNotEmpty(regex)) {
                    checkHandlers.add(beanFactory.getBean(RegexCheckImportHandler.class));
                }
                excelCheckImportHandlerMap.put(fieldName, CollectionUtil.distinct(checkHandlers));
            } else {
                excelCheckImportHandlerMap.put(fieldName, new ArrayList<>());
            }

            ExcelConvert excelConvert = field.getAnnotation(ExcelConvert.class);
            // 导入
            List<IConvertImportHandler> convertImportHandlers = new ArrayList<>();
            convertImportHandlers.add(beanFactory.getBean(ByTypeConvertImportHandler.class));
            // 导出
            List<IConvertExportHandler> convertExportHandlers = new ArrayList<>();
            convertExportHandlers.add(beanFactory.getBean(ByTypeConvertExportHandler.class));
            if (excelConvert != null) {
                excelConvertMap.put(fieldName, excelConvert);
                // 导入
                excelConvertDateParseMap.put(fieldName, excelConvert.dateParse());
                excelConvertDateParseTipsMap.put(fieldName, excelConvert.dateParseTips());
                Class<? extends IConvertImportHandler>[] importHandlerCls = excelConvert.convertImportHandlers();
                for (Class<? extends IConvertImportHandler> handler : importHandlerCls) {
                    convertImportHandlers.add(beanFactory.getBean(handler));
                }
                // 导出
                excelConvertDateFormatMap.put(fieldName, excelConvert.dateFormat());
                Class<? extends IConvertExportHandler>[] exportHandlers = excelConvert.convertExportHandlers();
                for (Class<? extends IConvertExportHandler> handler : exportHandlers) {
                    convertExportHandlers.add(beanFactory.getBean(handler));
                }
            }
            excelConvertImportHandlerMap.put(fieldName, CollectionUtil.distinct(convertImportHandlers));
            excelConvertExportHandlerMap.put(fieldName, CollectionUtil.distinct(convertExportHandlers));
        }
    }

    private void parseCls() {
        // 处理 ExcelEntity
        excelEntity = clazz.getAnnotation(ExcelEntity.class);
        if (excelEntity == null) {
            beanFactoryCls = SingletonBeanFactory.class;
            beanFactory = new SingletonBeanFactory();
            colIndexStrategy = ExcelEntity.ColIndexStrategyEnum.by_field_config;
            return;
        }
        beanFactoryCls = excelEntity.beanFactory();
        beanFactory = ClassUtil.newInstance(beanFactoryCls);
        cellConfigCls = excelEntity.cellConfig();
        cellConfig = beanFactory.getBean(cellConfigCls);
        colIndexStrategy = excelEntity.colIndexStrategy();
    }

    public <C> C newInstance() {
        return ClassUtil.newInstance(clazz);
    }

    public void setFieldValue(String fieldName, Object target, Object value) {
        ClassUtil.invoke(target, fieldSetMethodMap.get(fieldName), value);
    }

    public Object getFieldValue(String fieldName, Object target) {
        return ClassUtil.invoke(target, fieldGetMethodMap.get(fieldName));
    }

    public boolean isExport(String fieldName) {
        return this.getExcelExportMap().get(fieldName) != null;
    }

    public boolean isImport(String fieldName) {
        return this.getExcelImportMap().get(fieldName) != null;
    }
}
