package com.common.collect.container.excel.context;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.*;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.*;
import com.common.collect.container.excel.define.bean.SingletonBeanFactory;
import com.common.collect.container.excel.define.check.MaxCheckImportHandler;
import com.common.collect.container.excel.define.check.RegexCheckImportHandler;
import com.common.collect.container.excel.define.check.RequireCheckImportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertExportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertImportHandler;
import com.common.collect.util.CollectionUtil;
import com.common.collect.util.ConvertUtil;
import com.common.collect.util.EmptyUtil;
import com.google.common.collect.Lists;
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

    // class
    private Class<?> clazz;
    // ExcelEntity
    private ExcelEntity excelEntity;
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

    private static Map<Class, ExcelContext> cacheClassParseResult = new LinkedHashMap<>();

    public static ExcelContext excelContext(Class clazz) {
        return cacheClassParseResult.computeIfAbsent(clazz, (cls) ->
                new ExcelContext(clazz)
        );
    }

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

    private void init() {
        // 处理 类上的注解
        parseCls();
        // 处理 属性上的注解
        parseField();
    }

    private void parseField() {
        //  处理 导入参数
        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            String fieldName = entry.getKey();
            Field field = entry.getValue();
            ExcelImport excelImport = field.getAnnotation(ExcelImport.class);
            if (excelImport != null) {
                excelImportMap.put(fieldName, excelImport);
                String colIndex = excelImport.colIndex();
                if (EmptyUtil.isBlank(colIndex)) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "colIndex不能为空");
                } else {
                    excelImportColIndexMap.put(fieldName, excelImport.colIndex());
                }
                Class<? extends IColIndexParser> colIndexParserCls = excelImport.colIndexParser();
                excelImportColIndexParserClsMap.put(fieldName, colIndexParserCls);
                IColIndexParser colIndexParser = beanFactory.getBean(colIndexParserCls);
                if (colIndexParser == null) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "列下标解析器不能为空");
                }
                excelImportColIndexParserMap.put(fieldName, colIndexParser);
                List<Integer> colIndexNums = CollectionUtil.removeDuplicate(colIndexParser.parseColIndex(colIndex));
                excelImportColIndexNumMap.put(fieldName, colIndexNums);
                boolean isMultiCol = colIndexNums.size() > 1;
                if (isMultiCol && !fieldClsMap.get(fieldName).equals(List.class)) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "多列时类型必须是 List");
                }
                excelImportIsMultiColMap.put(fieldName, isMultiCol);
                if (isMultiCol) {
                    excelImportMultiColListTypeMap.put(fieldName, excelImport.dataType());
                } else {
                    excelImportMultiColListTypeMap.put(fieldName, fieldClsMap.get(fieldName));
                }
                excelImportTitleMap.put(fieldName, excelImport.title());
            }

            ExcelExport excelExport = field.getAnnotation(ExcelExport.class);
            if (excelExport != null) {
                excelExportMap.put(fieldName, excelExport);
                int colIndex = excelExport.colIndex();
                if (colIndex < 0) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "导出 colIndex 不能小于0");
                }
                excelExportColIndexMap.put(fieldName, colIndex);
                excelExportTitleMap.put(fieldName, excelExport.title());
                Class<? extends ICellConfig> cellConfigCls = excelExport.cellConfig();
                ICellConfig cellConfig = beanFactory.getBean(cellConfigCls);
                if (cellConfig != null) {
                    this.cellConfigCls = cellConfigCls;
                    this.cellConfig = cellConfig;
                }
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
                List<ICheckImportHandler> checkHandlers = Lists.newArrayList();
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
                excelCheckImportHandlerMap.put(fieldName, CollectionUtil.removeDuplicate(checkHandlers));
            } else {
                excelCheckImportHandlerMap.put(fieldName, new ArrayList<>());
            }

            ExcelConvert excelConvert = field.getAnnotation(ExcelConvert.class);
            // 导入
            List<IConvertImportHandler> convertImportHandlers = Lists.newArrayList();
            convertImportHandlers.add(beanFactory.getBean(ByTypeConvertImportHandler.class));
            // 导出
            List<IConvertExportHandler> convertExportHandlers = Lists.newArrayList();
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
            excelConvertImportHandlerMap.put(fieldName, CollectionUtil.removeDuplicate(convertImportHandlers));
            excelConvertExportHandlerMap.put(fieldName, CollectionUtil.removeDuplicate(convertExportHandlers));
        }
    }

    private void parseCls() {
        // 处理 ExcelEntity
        excelEntity = clazz.getAnnotation(ExcelEntity.class);
        if (excelEntity == null) {
            beanFactoryCls = SingletonBeanFactory.class;
            beanFactory = new SingletonBeanFactory();
            return;
        }
        beanFactoryCls = excelEntity.beanFactory();
        try {
            beanFactory = beanFactoryCls.newInstance();
        } catch (Exception ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "工厂类初始化失败", ex);
        }
        cellConfigCls = excelEntity.cellConfig();
        cellConfig = beanFactory.getBean(cellConfigCls);
    }


    public <C> C newInstance() {
        try {
            return (C) clazz.newInstance();
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "新建" + clazz.getName() + "出错", e);
        }
    }

    public void setFieldValue(String fieldName, Object target, Object value) {
        try {
            fieldSetMethodMap.get(fieldName).invoke(target, value);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "设值" + clazz.getName() + "出错", e);
        }
    }

    public Object getFieldValue(String fieldName, Object target) {
        try {
            return fieldGetMethodMap.get(fieldName).invoke(target);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "取值" + clazz.getName() + "出错", e);
        }
    }

    public boolean isExport(String fieldName) {
        return this.getExcelExportMap().get(fieldName) != null;
    }

    public boolean isImport(String fieldName) {
        return this.getExcelImportMap().get(fieldName) != null;
    }
}
