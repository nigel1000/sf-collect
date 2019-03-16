package com.common.collect.container.excel.pojo;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.ExcelEntity;
import com.common.collect.container.excel.annotations.model.ExcelEntityModel;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.util.ConvertUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by hznijianfeng on 2018/8/28.
 */

@Getter
public class ExcelParam<C> {

    @Data
    public static class FieldInfo {
        private Field field;
        private String fieldName;
        private Class fieldType;
        private Method fieldGetMethod;
        private String fieldGetMethodName;
        private Method fieldSetMethod;
        private String fieldSetMethodName;
    }

    @Data
    public static class ClassInfo<C> {
        private Class<C> target;

        private ExcelEntity excelEntity;
        private ExcelEntityModel excelEntityModel;
    }

    protected ClassInfo<C> classInfo;
    protected Map<String, FieldInfo> fieldInfoMap = Maps.newHashMap();

    public ExcelParam(@NonNull Class<C> target) {
        ClassInfo<C> classInfo = new ClassInfo<>();
        classInfo.setTarget(target);
        ExcelEntity excelEntity = target.getAnnotation(ExcelEntity.class);
        classInfo.setExcelEntity(excelEntity);
        classInfo.setExcelEntityModel(ExcelEntityModel.gen(excelEntity));
        this.classInfo = classInfo;
        Field[] fields = target.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setField(field);
            String fieldName = field.getName();
            fieldInfo.setFieldName(fieldName);
            Class fieldType = field.getType();
            fieldInfo.setFieldType(fieldType);

            String setMethodName = "set" + ConvertUtil.firstUpper(field.getName());
            String getMethodName = "get" + ConvertUtil.firstUpper(field.getName());
            fieldInfo.setFieldGetMethodName(getMethodName);
            fieldInfo.setFieldSetMethodName(setMethodName);
            try {
                fieldInfo.setFieldGetMethod(target.getMethod(getMethodName));
                fieldInfo.setFieldSetMethod(target.getMethod(setMethodName, fieldType));
            } catch (NoSuchMethodException ex) {
                throw UnifiedException.gen(ExcelConstants.MODULE, "解析" + this.getClassInfo().getTarget().getName() + "出错", ex);
            }
            fieldInfoMap.putIfAbsent(fieldName, fieldInfo);
        }
    }

    public C newInstance() {
        try {
            return this.getClassInfo().getTarget().newInstance();
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "新建" + this.getClassInfo().getTarget().getName() + "出错", e);
        }
    }

    public void setFieldValue(String fieldName, C target, Object value) {
        try {
            fieldInfoMap.get(fieldName).getFieldSetMethod().invoke(target, value);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "设值" + this.getClassInfo().getTarget().getName() + "出错", e);
        }
    }

    public Object getFieldValue(String fieldName, C target) {
        try {
            return fieldInfoMap.get(fieldName).getFieldGetMethod().invoke(target);
        } catch (Exception e) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "取值" + this.getClassInfo().getTarget().getName() + "出错", e);
        }
    }

}
