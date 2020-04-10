package com.common.collect.framework.docs;

import com.common.collect.framework.docs.model.DataTypeModel;
import com.common.collect.lib.api.docs.DocsMethod;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FunctionUtil;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.*;
import java.util.*;

/**
 * Created by hznijianfeng on 2020/3/16.
 */

public class DocsEntrance {

    public static DocsContext createDocs(@NonNull String pkg) {
        DocsContext docsContext = new DocsContext();
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(pkg);
        if (EmptyUtil.isEmpty(classList)) {
            return docsContext;
        }
        Map<String, DataTypeModel> docsDataTypes = new HashMap<>();
        for (Class<?> cls : classList) {
            DocsContext clsDocsContext = DocsEntrance.createDocs(cls);
            docsContext.addDocsInterface(clsDocsContext.getInterfaces());
            docsDataTypes.putAll(FunctionUtil.keyValueMap(clsDocsContext.getDataTypes(), DataTypeModel::getName));
        }
        docsContext.addDocsDataType(new ArrayList<>(docsDataTypes.values()));
        return docsContext;
    }

    public static DocsContext createDocs(@NonNull Class<?> cls) {
        DocsContext docsContext = new DocsContext();
        Map<String, DataTypeModel> docsDataTypes = new HashMap<>();
        for (Method method : ClassUtil.getDeclaredMethods(cls)) {
            DocsMethod docsMethod = method.getAnnotation(DocsMethod.class);
            RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
            if (docsMethod == null || methodRequestMapping == null) {
                continue;
            }
            DocsContext methodDocsContext = createDocs(method);
            docsContext.addDocsInterface(methodDocsContext.getInterfaces());
            docsDataTypes.putAll(FunctionUtil.keyValueMap(methodDocsContext.getDataTypes(), DataTypeModel::getName));
        }
        docsContext.addDocsDataType(new ArrayList<>(docsDataTypes.values()));
        return docsContext;
    }

    public static DocsContext createDocs(@NonNull Method declaredMethod) {
        Class<?> cls = declaredMethod.getDeclaringClass();
        DocsContext docsContext = new DocsContext();
        RequestMapping clsRequestMapping = cls.getAnnotation(RequestMapping.class);
        DocsMethod docsMethod = declaredMethod.getAnnotation(DocsMethod.class);
        RequestMapping methodRequestMapping = declaredMethod.getAnnotation(RequestMapping.class);
        if (docsMethod == null || methodRequestMapping == null) {
            return docsContext;
        }
        ParamContext paramContext = new ParamContext();
        paramContext.setCls(cls);
        paramContext.setReturnType(declaredMethod.getReturnType());
        paramContext.setReturnGenericTypeMap(ClassUtil.getMethodReturnGenericTypeMap(declaredMethod));
        paramContext.setMethod(declaredMethod);
        paramContext.setDocsMethod(docsMethod);
        paramContext.setClsRequestMapping(clsRequestMapping);
        paramContext.setMethodRequestMapping(methodRequestMapping);
        // 处理接口
        paramContext.createDocsInterface();
        // 处理接口入参
        paramContext.createDocsInterfaceParamInput();
        // 处理接口返回
        paramContext.createDocsInterfaceParamOutput();

        docsContext.addDocsInterface(paramContext.getDocsInterface());
        docsContext.addDocsDataType(new ArrayList<>(paramContext.getDocsDataTypes().values()));
        return docsContext;
    }

}
