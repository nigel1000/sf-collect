package com.common.collect.container.idoc;

import com.common.collect.util.EmptyUtil;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IDocFieldObj implements Serializable {
    // 名称
    private String name;
    private String nameDesc;
    // 类型
    private String type;
    private Class typeCls;
    private String arrayType;
    private Class arrayTypeCls;
    private Integer arrayTypeCount;
    // 默认值
    private Object value = null;
    // 描述
    private String desc;
    // 是否必须
    private boolean required;

    private IDocFieldType iDocFieldType;


    public boolean isArrayType() {
        return IDocFieldValueType.Array.name().equals(this.type) &&
                IDocFieldValueType.Object.name().equals(this.arrayType);
    }

    public void setArrayType(@NonNull Class arrayType, Integer arrayCount) {
        this.arrayType = IDocUtil.typeMapping(arrayType).name();
        this.arrayTypeCls = arrayType;
        this.arrayTypeCount = arrayCount;
        Object defValue = IDocUtil.typeDefaultValue(arrayType);
        if (this.value == null && defValue != null) {
            this.setValue(IDocUtil.arrayCountList(IDocUtil.typeDefaultValue(arrayType), arrayCount));
        }
    }

    public static IDocFieldObj of(IDocField iDocField, @NonNull Class type, @NonNull IDocFieldType iDocFieldType) {
        IDocFieldObj docFieldObj = new IDocFieldObj();
        docFieldObj.setValue(IDocUtil.typeDefaultValue(type));
        if (iDocField != null) {
            docFieldObj.setNameDesc(iDocField.nameDesc());
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
}