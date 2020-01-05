package com.common.collect.debug.spring;


import com.common.collect.container.JsonUtil;
import com.common.collect.container.SpringContextUtil;
import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by nijianfeng on 2020/1/5.
 */
@RestController
@Slf4j
public class BeanController {


    private String bean_key = "c26b094cc27346379266147682c41fc0";

    // /back/door/bean/invoke?beanName=flowLogManager&methodName=list&args=["BACK_DOOR_BEAN_INVOKE", null, 1, 10]&uuid=c26b094cc27346379266147682c41fc0
    @RequestMapping(value = "/back/door/bean/invoke", method = {RequestMethod.GET})
    public Object beanInvoke(@RequestParam(value = "beanName") String beanName,
                             @RequestParam(value = "methodName") String methodName,
                             @RequestParam(value = "args") String args,
                             @RequestParam(value = "uuid") String uuid) {

        if (!uuid.equals(bean_key)) {
            return "无权限访问此链接";
        }
        Object obj = SpringContextUtil.getBean(beanName);
        if (obj == null) {
            return "无此 " + beanName + " bean";
        }
        try {
            List<String> params = JsonUtil.json2beanList(args, String.class);
            Method invoke = null;
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == params.size()) {
                    invoke = method;
                    break;
                }
            }
            if (invoke == null) {
                return "无此 " + methodName + "方法";
            }

            Class<?>[] clazz = invoke.getParameterTypes();
            Object[] methodParams = new Object[clazz.length];
            for (int i = 0; i < clazz.length; i++) {
                try {
                    methodParams[i] = JsonUtil.json2bean(params.get(i), clazz[i]);
                } catch (Exception ex) {
                    methodParams[i] = JsonUtil.json2bean("\"" + params.get(i) + "\"", clazz[i]);
                }
            }
            return invoke.invoke(obj, methodParams);
        } catch (Exception ex) {
            return StringUtil.format("", ex);
        }
    }


}
