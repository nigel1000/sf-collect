package com.common.collect.container.excel.context;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.ExcelCheck;
import com.common.collect.container.excel.annotations.ExcelConvert;
import com.common.collect.container.excel.annotations.ExcelEntity;
import com.common.collect.container.excel.annotations.ExcelExport;
import com.common.collect.container.excel.annotations.ExcelImport;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.define.IColIndexParser;
import com.common.collect.container.excel.define.IConvertExportHandler;
import com.common.collect.container.excel.define.IConvertImportHandler;
import com.common.collect.container.excel.define.bean.SingletonBeanFactory;
import com.common.collect.container.excel.define.check.MaxCheckImportHandler;
import com.common.collect.container.excel.define.check.RegexCheckImportHandler;
import com.common.collect.container.excel.define.check.RequireCheckImportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertExportHandler;
import com.common.collect.container.excel.define.convert.ByTypeConvertImportHandler;
import com.common.collect.util.ClassUtil;
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
    private boolean colIndexSortByField;
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

    private static Map<Class, ExcelContext> cacheClassParseResult = new LinkedHashMap<>();

    public static ExcelContext excelContext(Class clazz) {
        return cacheClassParseResult.computeIfAbsent(clazz, (cls) -> new ExcelContext(clazz));
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
                    throw UnifiedException.gen(ExcelConstants.MODULE, "列下标解析器不能为空");
                }
                excelImportColIndexParserMap.put(fieldName, colIndexParser);
                List<Integer> colIndexNums = new ArrayList<>();
                if (colIndexSortByField) {
                    colIndexNums.add(fieldImportIndex++);
                }
                if (EmptyUtil.isNotBlank(colIndex)) {
                    colIndexNums = CollectionUtil.removeDuplicate(colIndexParser.parseColIndex(colIndex));
                }
                if (EmptyUtil.isEmpty(colIndexNums)) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "colIndex不能为空");
                }
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

            if (isExport(fieldName)) {
                ExcelExport excelExport = excelExportMap.get(fieldName);
                Integer colIndex = null;
                if (colIndexSortByField) {
                    colIndex = fieldExportIndex++;
                }
                if (excelExport.colIndex() != -1) {
                    colIndex = excelExport.colIndex();
                }
                if (colIndex == null || colIndex < 0) {
                    throw UnifiedException.gen(ExcelConstants.MODULE, "导出 colIndex 不能小于0");
                }
                excelExportColIndexMap.put(fieldName, colIndex);
                excelExportTitleMap.put(fieldName, excelExport.title());
                Class<? extends ICellConfig> cellConfigCls = excelExport.cellConfig();
                excelExportCellConfigClsMap.put(fieldName, cellConfigCls);
                ICellConfig cellConfig = ConvertUtil.selectAfter(this.cellConfig, beanFactory.getBean(cellConfigCls));
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
            colIndexSortByField = false;
            return;
        }
        beanFactoryCls = excelEntity.beanFactory();
        beanFactory = ClassUtil.newInstance(beanFactoryCls);
        cellConfigCls = excelEntity.cellConfig();
        cellConfig = beanFactory.getBean(cellConfigCls);
        colIndexSortByField = excelEntity.colIndexSortByField();
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
