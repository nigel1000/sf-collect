package com.common.collect.framework.docs;

import com.common.collect.lib.api.docs.DocsDataType;
import com.common.collect.lib.api.docs.DocsField;
import com.common.collect.lib.util.EmptyUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/3/16.
 */

@Data
public class DocsContext {

    private List<Interface> interfaces = new ArrayList<>();
    private List<DataType> dataTypes = new ArrayList<>();

    public DocsContext addDocsInterface(Interface docsInterface) {
        interfaces.add(docsInterface);
        return this;
    }

    public DocsContext addDocsDataType(Collection<DataType> docDataTypes) {
        dataTypes.addAll(docDataTypes);
        return this;
    }

    public DocsContext addDocsDataType(DataType docDataType) {
        dataTypes.add(docDataType);
        return this;
    }

    public boolean hasInterfaces() {
        return interfaces.size() > 0;
    }

    @Data
    public static class DataType {
        // 该数据类型的名称
        private String name;
        // 该数据类型的介绍
        private String description;
        // 对数据类型自身类型的定义
        private int format = DataTypeFormatEnum.Object.getType();
        // 对数据类型分类的定义
        private int type = DataTypeTypeEnum.User.getType();
        // 该数据模型包含的所有参数数组
        private List<Parameter> params = new ArrayList<>();

        public static DataType gen(@NonNull Class<?> cls, DocsDataType docsDataType) {
            DocsContext.DataType dataType = new DocsContext.DataType();
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

        public DataType addDocsParameter(Parameter docsParameter) {
            params.add(docsParameter);
            return this;
        }

        public DataType addDocsParameter(List<Parameter> docsParameters) {
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

    @Data
    public static class Interface {
        // 该接口的名称
        private String name;
        // 该接口的代码映射
        private String className;
        // 该接口的介绍
        private String description;
        // 接口方法
        private String method = InterfaceMethodEnum.GET.name();
        // 该接口访问路径
        private String path;

        private InterfaceParameter params = new InterfaceParameter();

        public enum InterfaceMethodEnum {
            GET,
            POST,
            HEAD,
            PATCH,
            PUT,
            DELETE,
            ;
        }
    }

    @Data
    public static class Parameter {
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

        public static Parameter gen(@NonNull Class<?> cls, RequestParam requestParam, DocsField docsField, String name) {
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
            Parameter docsParameter = new Parameter();
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

        public enum DataTypeNameEnum {
            Number,
            String,
            Boolean,
            ;

            public static boolean isBaseDataTypeName(@NonNull String value) {
                List<String> enums = new ArrayList<>();
                for (DataTypeNameEnum typeNameEnum : DataTypeNameEnum.values()) {
                    enums.add(typeNameEnum.getName());
                }
                return enums.contains(value.toLowerCase());
            }

            public String getName() {
                return this.name().toLowerCase();
            }
        }
    }

    @Data
    public static class InterfaceParameter {
        //  该接口的请求参数
        private List<Parameter> inputs = new ArrayList<>();
        // 该接口的响应参数
        private List<Parameter> outputs = new ArrayList<>();

        public InterfaceParameter addInput(Parameter parameter) {
            inputs.add(parameter);
            return this;
        }

        public InterfaceParameter addInput(List<Parameter> parameters) {
            inputs.addAll(parameters);
            return this;
        }

        public InterfaceParameter addOutput(Parameter parameter) {
            outputs.add(parameter);
            return this;
        }

        public InterfaceParameter addOutput(List<Parameter> parameters) {
            outputs.addAll(parameters);
            return this;
        }
    }

}
