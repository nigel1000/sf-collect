package com.common.collect.framework.docs.model;

import com.common.collect.lib.api.docs.DocsDataType;
import com.common.collect.lib.util.EmptyUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/4/8.
 */
@Data
public class DataTypeModel {
    // 该数据类型的名称
    private String name;
    // 该数据类型的介绍
    private String description;
    // 对数据类型自身类型的定义
    private int format = DataTypeFormatEnum.Object.getType();
    // 对数据类型分类的定义
    private int type = DataTypeTypeEnum.User.getType();
    // 该数据模型包含的所有参数数组
    private List<ParameterModel> params = new ArrayList<>();

    public static DataTypeModel gen(@NonNull Class<?> cls, DocsDataType docsDataType) {
        DataTypeModel dataType = new DataTypeModel();
        dataType.setName(cls.getName());
        if (docsDataType != null) {
            if (EmptyUtil.isNotBlank(docsDataType.name())) {
                dataType.setName(docsDataType.name());
            }
            if (EmptyUtil.isNotBlank(docsDataType.desc())) {
                dataType.setDescription(docsDataType.desc());
            }
        }
        return dataType;
    }

    public DataTypeModel addDocsParameter(ParameterModel docsParameter) {
        params.add(docsParameter);
        return this;
    }

    public DataTypeModel addDocsParameter(List<ParameterModel> docsParameters) {
        params.addAll(docsParameters);
        return this;
    }

    @Getter
    public enum DataTypeFormatEnum {
        Object(0),
        ;

        private int type;

        DataTypeFormatEnum(int type) {
            this.type = type;
        }
    }

    @Getter
    public enum DataTypeTypeEnum {
        User(0),
        ;
        private int type;

        DataTypeTypeEnum(int type) {
            this.type = type;
        }
    }
}
