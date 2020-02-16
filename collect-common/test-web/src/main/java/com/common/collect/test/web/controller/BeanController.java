package com.common.collect.test.web.controller;

import com.common.collect.lib.util.StringUtil;
import com.common.collect.lib.util.fastjson.JsonUtil;
import com.common.collect.lib.util.spring.SpringContextUtil;
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
@RequestMapping("/back/door/bean")
@Slf4j
public class BeanController {


    // http://localhost:8181/back/door/bean/invoke?beanName=redisClient&methodName=getValueWrapper&args=%5B"redis"%5D
    @RequestMapping(value = "/invoke", method = {RequestMethod.GET})
    public Object beanInvoke(@RequestParam(value = "beanName") String beanName,
                             @RequestParam(value = "methodName") String methodName,
                             @RequestParam(value = "args") String args) {

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
