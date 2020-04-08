package com.common.collect.framework.docs;

import com.common.collect.framework.docs.model.DataTypeModel;
import com.common.collect.framework.docs.model.InterfaceModel;
import com.common.collect.framework.docs.model.ParameterModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hznijianfeng on 2020/3/16.
 */

@Data
public class DocsContext {

    private List<InterfaceModel> interfaces = new ArrayList<>();
    private List<DataTypeModel> dataTypes = new ArrayList<>();

    public DocsContext addDocsInterface(InterfaceModel docsInterface) {
        interfaces.add(docsInterface);
        return this;
    }

    public DocsContext addDocsInterface(Collection<InterfaceModel> docsInterfaces) {
        interfaces.addAll(docsInterfaces);
        return this;
    }


    public DocsContext addDocsDataType(Collection<DataTypeModel> docDataTypes) {
        dataTypes.addAll(docDataTypes);
        return this;
    }

    public DocsContext addDocsDataType(DataTypeModel docDataType) {
        dataTypes.add(docDataType);
        return this;
    }

    public boolean hasInterfaces() {
        return interfaces.size() > 0;
    }

}
