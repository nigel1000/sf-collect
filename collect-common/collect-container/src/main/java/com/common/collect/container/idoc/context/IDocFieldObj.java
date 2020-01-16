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
    private String type;
    private Class typeCls;
    private String arrayType;
    private Class arrayTypeCls;
    private Integer arrayTypeCount;
    // 默认值
    // type = Object || arrayType = Object 时可能是map，当map为空时并且IDocField里value不为空时是string
    private Object value;
    // 描述
    private String desc;
    // 是否必须
    private boolean required;

    private IDocFieldType iDocFieldType;

    public static IDocFieldObj of(IDocField iDocField, @NonNull Class type, @NonNull IDocFieldType iDocFieldType) {
        IDocFieldObj docFieldObj = new IDocFieldObj();
        docFieldObj.setValue(IDocUtil.typeDefaultValue(type));
        if (iDocField != null) {
            docFieldObj.setDesc(iDocField.desc());
            if (EmptyUtil.isNotEmpty(iDocField.value())) {
                docFieldObj.setValue(iDocField.value());
            }
            if (IDocFieldType.request == iDocFieldType) {
                docFieldObj.setRequired(iDocField.required());
            }
        }
        docFieldObj.setType(IDocUtil.typeMapping(type).name());
        docFieldObj.setTypeCls(type);
        docFieldObj.setIDocFieldType(iDocFieldType);
        return docFieldObj;
    }

    public boolean isArrayType() {
        return IDocFieldValueType.Array.name().equals(this.type);
    }

    public boolean isArrayObjectType() {
        return isArrayType() && IDocFieldValueType.Object.name().equals(this.arrayType);
    }

    public boolean isObjectType() {
        return IDocFieldValueType.Object.name().equals(this.type);
    }

    public void setArrayType(@NonNull Class arrayType, Integer arrayCount) {
        this.arrayType = IDocUtil.typeMapping(arrayType).name();
        this.arrayTypeCls = arrayType;
        this.arrayTypeCount = arrayCount;
        this.setValue(IDocUtil.typeDefaultValue(arrayType));

    }
}