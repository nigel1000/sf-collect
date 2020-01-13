package com.common.collect.container.idoc;


import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class IDocFieldObjFromClassContext {

    private Set<String> traceCurrentClass = null;

    public void validTrace(@NonNull Class cls) {
        if (traceCurrentClass == null) {
            traceCurrentClass = new LinkedHashSet<>();
        }
        String traceName = cls.getSimpleName();
        if (traceCurrentClass.contains(traceName)) {
            List<String> log = new ArrayList<>(traceCurrentClass);
            log.add(traceName);
            throw UnifiedException.gen("循环依赖：" + JsonUtil.bean2json(log));
        }
        traceCurrentClass.add(traceName);
    }

}