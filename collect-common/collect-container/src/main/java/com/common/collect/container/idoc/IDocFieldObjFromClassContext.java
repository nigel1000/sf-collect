package com.common.collect.container.idoc;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class IDocFieldObjFromClassContext {

    private IDocFieldType docFieldType;
    private List<String> traceCurrentClass = new ArrayList<>();
    private Map<String, Type> genericTypeMap = new LinkedHashMap<>();

    public IDocFieldObjFromClassContext(IDocFieldType docFieldType) {
        this.docFieldType = docFieldType;
    }

    public void enter(@NonNull Class cls) {
        if (traceCurrentClass == null) {
            traceCurrentClass = new ArrayList<>();
        }
        String traceName = cls.getName();
        if (traceCurrentClass.contains(traceName)) {
            traceCurrentClass.add(traceName);
            throw UnifiedException.gen("循环依赖：" + JsonUtil.bean2json(traceCurrentClass));
        }
        traceCurrentClass.add(traceName);
    }

    public void exit() {
        if (EmptyUtil.isEmpty(traceCurrentClass)) {
            return;
        }
        traceCurrentClass.remove(traceCurrentClass.size() - 1);
    }

}