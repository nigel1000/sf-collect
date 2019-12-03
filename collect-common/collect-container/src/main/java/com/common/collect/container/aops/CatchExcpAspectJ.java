package com.common.collect.container.aops;

import com.common.collect.api.Constants;
import com.common.collect.api.Response;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.AopUtil;
import com.common.collect.container.trace.TraceIdUtil;
import com.common.collect.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by hznijianfeng on 2018/8/15. 加在provider上，譬如facade等
 */

@Aspect
@Component
@Order(value = 0)
@Slf4j
public class CatchExcpAspectJ {

    @Pointcut("@within(com.common.collect.container.aops.CatchExcp)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.container.aops.CatchExcp)")
    public void method() {
    }

    @Around("clazz() || method()")
    public Object around(final ProceedingJoinPoint point) {

        CatchExcp catchExcp = AopUtil.getAnnotation(point, CatchExcp.class);
        String module = catchExcp.module();
        return errorHandler(point, module);
    }

    private Object errorHandler(final ProceedingJoinPoint point, String module) {

        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Class returnType = ((MethodSignature) point.getSignature()).getReturnType();
        try {
            return point.proceed();
        } catch (UnifiedException ex) {
            if (ex.getCause() != null) {
                try {
                    log.error("UnifiedException 异常。模块：{}，类名：{}，方法：{}，入参：{}，描述:{}, 上下文:{}",
                            module, className, methodName,
                            point.getArgs(),
                            ex.getErrorMessage(), ex.getContext(),
                            ex);
                } catch (Exception exception) {
                }
            }
            return returnResult(ex, returnType);
        } catch (Exception ex) {
            try {
                log.error("Exception 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        point.getArgs(),
                        ex);
            } catch (Exception exception) {
            }
            return returnResult(Constants.ERROR, Constants.errorFromSystem, returnType);
        } catch (Throwable ex) {
            try {
                log.error("Throwable 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        point.getArgs(),
                        ex);
            } catch (Exception exception) {
            }
            return returnResult(Constants.ERROR, Constants.errorFromSystem, returnType);
        }
    }

    private Object returnResult(int errorCode, String errorMessage, Class returnType) {

        if (Response.class == returnType) {
            Response response = Response.fail(errorCode, errorMessage);
            response.addContext("traceId", TraceIdUtil.traceId());
            return response;
        }

        return ClassUtil.returnBaseDataType(returnType);
    }

    private Object returnResult(UnifiedException ex, Class returnType) {
        if (Response.class == returnType) {
            Response response = Response.fail(ex.getErrorCode(), ex.getErrorMessage());
            response.addContext("traceId", TraceIdUtil.traceId());
            response.addContext(ex.getContext());
            return response;
        }

        return ClassUtil.returnBaseDataType(returnType);
    }

}
