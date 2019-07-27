package com.common.collect.debug.spring;

import com.common.collect.util.ThreadLocalUtil;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AjaxInterceptor implements HandlerInterceptor {

    public static final String ajax_rtn_type = "ajax_rtn_type";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();
        boolean isResponseBody = method.isAnnotationPresent(ResponseBody.class);
        boolean isRestController = hm.getBeanType().isAnnotationPresent(RestController.class);
        if (isResponseBody || isRestController) {
            ThreadLocalUtil.push(ajax_rtn_type, method.getReturnType());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object o, Exception e) throws Exception {
        ThreadLocalUtil.clear(ajax_rtn_type);
    }
}
