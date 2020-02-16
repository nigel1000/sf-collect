package com.common.collect.framework.docs.context;

import com.common.collect.framework.docs.base.DocsFieldType;
import com.common.collect.framework.docs.base.DocsFieldValueType;
import com.common.collect.framework.docs.util.DocsUtil;
import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.util.EmptyUtil;
import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocsFieldObj implements Serializable {
    // 名称
    private String name;
    // 类型
    private DocsFieldValueType type;
    private Class<?> typeCls;
    private DocsFieldValueType arrayType;
    private Class<?> arrayTypeCls;
    private Integer arrayTypeCount;
    // 默认值
    private Object defValue;
    // 描述
    private String desc;
    // 是否必须
    private boolean required;

    private DocsFieldType docsFieldType;

    public static DocsFieldObj of(DocsField docsField, @NonNull Class type, @NonNull DocsFieldType docsFieldType) {
        DocsFieldObj docFieldObj = new DocsFieldObj();
        docFieldObj.setDocsFieldType(docsFieldType);
        docFieldObj.setType(DocsUtil.typeMapping(type));
        docFieldObj.setTypeCls(type);
        docFieldObj.setDefValue(DocsUtil.typeDefaultValue(type));
        docFieldObj.setRequired(true);
        if (docsField != null) {
            docFieldObj.setDesc(docsField.desc());
            if (EmptyUtil.isNotEmpty(docsField.value())) {
                docFieldObj.setDefValue(docsField.value());
            }
            if (DocsFieldType.request == docsFieldType) {
                docFieldObj.setRequired(docsField.required());
            }
        }
        return docFieldObj;
    }

    public boolean isObjectValue() {
        return this.defValue instanceof Map;
    }

    public boolean isUnKnowType() {
        return DocsFieldValueType.UnKnow.equals(this.type);
    }

    public boolean isBaseType() {
        return !isObjectType() && !isUnKnowType() && !isArrayType();
    }

    public boolean isObjectType() {
        return DocsFieldValueType.Object.equals(this.type);
    }

    public boolean isArrayType() {
        return DocsFieldValueType.Array.equals(this.type);
    }

    public boolean isArrayBaseType() {
        return isArrayType() && !isArrayObjectType() && !isArrayUnKnowType();
    }

    public boolean isArrayObjectType() {
        return isArrayType() && DocsFieldValueType.Object.equals(this.arrayType);
    }

    public boolean isArrayUnKnowType() {
        return isArrayType() && DocsFieldValueType.UnKnow.equals(this.arrayType);
    }

    public void setArrayType(@NonNull Class arrayType, Integer arrayCount) {
        this.arrayType = DocsUtil.typeMapping(arrayType);
        this.arrayTypeCls = arrayType;
        this.arrayTypeCount = arrayCount;
        this.setDefValue(DocsUtil.typeDefaultValue(arrayType));
    }

    public Map<String, Object> getDefValueMock() {
        Map<String, Object> bean = new LinkedHashMap<>();
        String showKey = this.name;
        if (isUnKnowType()) {
            bean.put(showKey, this.defValue);
            return bean;
        }
        if (isBaseType()) {
            bean.put(showKey, this.defValue);
            return bean;
        }
        if (isObjectType()) {
            if (isObjectValue()) {
                Map<String, DocsFieldObj> objFieldMap = (Map<String, DocsFieldObj>) this.defValue;
                Map<String, Object> objMap = new LinkedHashMap<>();
                objFieldMap.forEach((k, v) -> {
                    objMap.putAll(v.getDefValueMock());
                });
                bean.put(showKey, objMap);
                return bean;
            }
            bean.put(showKey, this.defValue);
            return bean;
        }
        if (isArrayType()) {
            if (isArrayBaseType()) {
                bean.put(showKey, DocsUtil.arrayCountList(this.defValue, this.arrayTypeCount));
                return bean;
            }
            if (isArrayUnKnowType()) {
                bean.put(showKey, this.defValue);
                return bean;
            }
            if (isArrayObjectType()) {
                if (isObjectValue()) {
                    Map<String, DocsFieldObj> objFieldMap = (Map<String, DocsFieldObj>) this.defValue;
                    Map<String, Object> objMap = new LinkedHashMap<>();
                    for (Map.Entry<String, DocsFieldObj> entry : objFieldMap.entrySet()) {
                        String k = entry.getKey();
                        DocsFieldObj v = entry.getValue();
                        objMap.putAll(v.getDefValueMock());
                    }
                    bean.put(showKey, DocsUtil.arrayCountList(objMap, this.getArrayTypeCount()));
                    return bean;
                }
                bean.put(showKey, this.defValue);
                return bean;
            }
        }

        return bean;
    }

}