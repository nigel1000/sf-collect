package com.common.collect.container.idoc.context;

import com.common.collect.api.idoc.IDocField;
import com.common.collect.container.idoc.base.IDocFieldType;
import com.common.collect.container.idoc.base.IDocFieldValueType;
import com.common.collect.container.idoc.util.IDocUtil;
import com.common.collect.util.EmptyUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IDocFieldObj implements Serializable {
    // 名称
    private String name;
    // 类型
    private IDocFieldValueType type;
    private Class<?> typeCls;
    private IDocFieldValueType arrayType;
    private Class<?> arrayTypeCls;
    private Integer arrayTypeCount;
    // 默认值
    private Object defValue;
    // 描述
    private String desc;
    // 是否必须
    private boolean required;

    private IDocFieldType iDocFieldType;

    public static IDocFieldObj of(IDocField iDocField, @NonNull Class type, @NonNull IDocFieldType iDocFieldType) {
        IDocFieldObj docFieldObj = new IDocFieldObj();
        docFieldObj.setIDocFieldType(iDocFieldType);
        docFieldObj.setType(IDocUtil.typeMapping(type));
        docFieldObj.setTypeCls(type);
        docFieldObj.setDefValue(IDocUtil.typeDefaultValue(type));
        docFieldObj.setRequired(true);
        if (iDocField != null) {
            docFieldObj.setDesc(iDocField.desc());
            if (EmptyUtil.isNotEmpty(iDocField.value())) {
                docFieldObj.setDefValue(iDocField.value());
            }
            if (IDocFieldType.request == iDocFieldType) {
                docFieldObj.setRequired(iDocField.required());
            }
        }
        return docFieldObj;
    }

    public boolean isObjectValue() {
        return this.defValue instanceof Map;
    }

    public boolean isUnKnowType() {
        return IDocFieldValueType.UnKnow.equals(this.type);
    }

    public boolean isBaseType() {
        return !isObjectType() && !isUnKnowType() && !isArrayType();
    }

    public boolean isObjectType() {
        return IDocFieldValueType.Object.equals(this.type);
    }

    public boolean isArrayType() {
        return IDocFieldValueType.Array.equals(this.type);
    }

    public boolean isArrayBaseType() {
        return isArrayType() && !isArrayObjectType() && !isArrayUnKnowType();
    }

    public boolean isArrayObjectType() {
        return isArrayType() && IDocFieldValueType.Object.equals(this.arrayType);
    }

    public boolean isArrayUnKnowType() {
        return isArrayType() && IDocFieldValueType.UnKnow.equals(this.arrayType);
    }

    public void setArrayType(@NonNull Class arrayType, Integer arrayCount) {
        this.arrayType = IDocUtil.typeMapping(arrayType);
        this.arrayTypeCls = arrayType;
        this.arrayTypeCount = arrayCount;
        this.setDefValue(IDocUtil.typeDefaultValue(arrayType));
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
                Map<String, IDocFieldObj> objFieldMap = (Map<String, IDocFieldObj>) this.defValue;
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
                bean.put(showKey, IDocUtil.arrayCountList(this.defValue, this.arrayTypeCount));
                return bean;
            }
            if (isArrayUnKnowType()) {
                bean.put(showKey, this.defValue);
                return bean;
            }
            if (isArrayObjectType()) {
                if (isObjectValue()) {
                    Map<String, IDocFieldObj> objFieldMap = (Map<String, IDocFieldObj>) this.defValue;
                    Map<String, Object> objMap = new LinkedHashMap<>();
                    for (Map.Entry<String, IDocFieldObj> entry : objFieldMap.entrySet()) {
                        String k = entry.getKey();
                        IDocFieldObj v = entry.getValue();
                        objMap.putAll(v.getDefValueMock());
                    }
                    bean.put(showKey, IDocUtil.arrayCountList(objMap, this.getArrayTypeCount()));
                    return bean;
                }
                bean.put(showKey, this.defValue);
                return bean;
            }
        }

        return bean;
    }

}