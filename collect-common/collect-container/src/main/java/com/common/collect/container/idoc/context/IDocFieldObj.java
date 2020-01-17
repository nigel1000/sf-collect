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
        docFieldObj.setDefValue(IDocUtil.typeDefaultValue(type));
        if (iDocField != null) {
            docFieldObj.setDesc(iDocField.desc());
            if (EmptyUtil.isNotEmpty(iDocField.value())) {
                docFieldObj.setDefValue(iDocField.value());
            }
            if (IDocFieldType.request == iDocFieldType) {
                docFieldObj.setRequired(iDocField.required());
            }
        }
        docFieldObj.setType(IDocUtil.typeMapping(type));
        docFieldObj.setTypeCls(type);
        docFieldObj.setIDocFieldType(iDocFieldType);
        return docFieldObj;
    }

    public boolean isArrayType() {
        return IDocFieldValueType.Array.equals(this.type);
    }

    public boolean isArrayObjectType() {
        return isArrayType() && IDocFieldValueType.Object.equals(this.arrayType);
    }

    public boolean isObjectType() {
        return IDocFieldValueType.Object.equals(this.type);
    }

    public void setArrayType(@NonNull Class arrayType, Integer arrayCount) {
        this.arrayType = IDocUtil.typeMapping(arrayType);
        this.arrayTypeCls = arrayType;
        this.arrayTypeCount = arrayCount;
        this.setDefValue(IDocUtil.typeDefaultValue(arrayType));

    }
}