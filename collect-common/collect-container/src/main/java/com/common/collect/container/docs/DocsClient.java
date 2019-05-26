package com.common.collect.container.docs;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.TemplateUtil;
import com.common.collect.util.ClassUtil;
import com.common.collect.util.FileUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Slf4j
@Data
public class DocsClient {

    public static void createDocsApi(DocsGlobalConfig globalConfig) {
        globalConfig.valid();

        List<TplContext> tplContexts = new ArrayList<>();
        List<Class<?>> classList = ClassUtil.getClazzFromPackage(globalConfig.getPkgPath());
        for (Class<?> cls : classList) {
            DocsApi docsApi = cls.getAnnotation(DocsApi.class);
            for (Method method : getMethods(cls)) {
                log.info("createDocsApi className:{},methodName:{}", cls.getName(), method.getName());
                DocsApiMethod docsApiMethod = method.getAnnotation(DocsApiMethod.class);
                DocsMethodConfig docsMethodConfig;
                try {
                    int argsLength = method.getParameterCount();
                    Object[] args = new Object[argsLength];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = null;
                    }
                    method.setAccessible(true);
                    docsMethodConfig = (DocsMethodConfig) method.invoke(cls.newInstance(), args);
                } catch (Exception ex) {
                    throw UnifiedException.gen("反射调用失败", ex);
                }
                if (docsMethodConfig == null) {
                    throw UnifiedException.gen("class:" + cls.getName() + ",method:" + method.getName() + ",获取返回数据失败");
                }
                TplContext tplContext = TplContext.build(globalConfig, docsApi, docsApiMethod, docsMethodConfig);
                tplContexts.add(tplContext);
            }
        }
        for (TplContext tplContext : tplContexts) {
            Map<String, Object> tplMap = new HashMap<>();
            tplMap.put("tplContext", tplContext);
            log.debug("Create DocsApi: filePath:{},tplMap:{}", tplContext.getSavePath(), tplMap);
            String fileContent = TemplateUtil.genTemplate("/tpl", "docs.tpl", tplMap);
            FileUtil.createFile(tplContext.getSavePath(), false, fileContent.getBytes(), tplContext.isReCreate());
        }
    }

    private static List<Method> getMethods(@NonNull Class<?> cls) {
        List<Method> ret = new ArrayList<>();
        for (Method method : cls.getDeclaredMethods()) {
            DocsApiMethod docsApiMethod = method.getAnnotation(DocsApiMethod.class);
            if (docsApiMethod == null) {
                continue;
            }
            if (!method.getReturnType().equals(DocsMethodConfig.class)) {
                continue;
            }
            ret.add(method);
        }
        ret.sort(Comparator.comparingLong(m -> m.getAnnotation(DocsApiMethod.class).order()));
        return ret;
    }

}
