package com.common.collect.framework.docs.model;

import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/4/8.
 */
@Data
public class ParameterModel {
    //  该值的类型定义，比如系统类型"string", "number", "boolean", 以及 DocsDataType.name
    private String dataTypeName;
    // 该数据参数的名称
    private String name;
    // 该数据类型的介绍
    private String description;
    // 该参数的默认值
    private String defaultValue;
    private Object mockValue;
    private boolean isArray = false;
    private Integer arrayCount;
    private boolean required;

    public static ParameterModel gen(@NonNull Class<?> cls, RequestParam requestParam, DocsField docsField, String name) {
        String description = "";
        String defaultValue = "";
        boolean required = false;
        if (docsField != null) {
            if (EmptyUtil.isNotBlank(docsField.desc())) {
                description = docsField.desc();
            }
            if (EmptyUtil.isNotEmpty(docsField.defaultValue())) {
                defaultValue = docsField.defaultValue();
            }
            required = docsField.required();
        }
        if (requestParam != null) {
            if (EmptyUtil.isNotBlank(requestParam.name())) {
                name = requestParam.name();
            }
            if (!ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                defaultValue = requestParam.defaultValue();
            }
            required = requestParam.required();
        }
        ParameterModel docsParameter = new ParameterModel();
        docsParameter.setName(name);
        docsParameter.setDescription(description);
        docsParameter.setDefaultValue(defaultValue);
        docsParameter.setRequired(required);
        if (EmptyUtil.isNotBlank(defaultValue)) {
            docsParameter.setMockValue(defaultValue);
        }
        return docsParameter;
    }

    public boolean hasDataTypeName() {
        return EmptyUtil.isNotBlank(dataTypeName);
    }

    public enum BaseDataTypeNameEnum {
        Number,
        String,
        Boolean,
        ;

        public static boolean isBaseDataTypeName(@NonNull String value) {
            List<String> enums = new ArrayList<>();
            for (BaseDataTypeNameEnum typeNameEnum : BaseDataTypeNameEnum.values()) {
                enums.add(typeNameEnum.getName());
            }
            return enums.contains(value.toLowerCase());
        }

        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
