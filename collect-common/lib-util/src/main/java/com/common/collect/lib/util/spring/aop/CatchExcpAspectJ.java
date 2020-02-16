package com.common.collect.lib.util.spring.aop;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.enums.CodeEnum;
import com.common.collect.lib.api.excps.IBizException;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.TraceIdUtil;
import com.common.collect.lib.util.spring.AopUtil;
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

    @Pointcut("@within(com.common.collect.lib.util.spring.aop.CatchExcp)")
    public void clazz() {
    }

    @Pointcut("@annotation(com.common.collect.lib.util.spring.aop.CatchExcp)")
    public void method() {
    }

    @Around("clazz() || method()")
    public Object around(final ProceedingJoinPoint point) {
        CatchExcp catchExcp = AopUtil.getAnnotation(point, CatchExcp.class);
        try {
            return point.proceed();
        } catch (Throwable ex) {
            return excpResult(ex, point, catchExcp);
        }
    }

    private Object excpResult(Throwable bizExcp, ProceedingJoinPoint point, CatchExcp catchExcp) {
        String module = catchExcp.module();
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Class returnType = ((MethodSignature) point.getSignature()).getReturnType();
        try {
            if (bizExcp instanceof IBizException) {
                IBizException bizException = (IBizException) bizExcp;
                if (bizExcp.getCause() != null) {
                    log.error("{} 异常。模块：{}，类名：{}，方法：{}，入参：{}，描述:{}, 上下文:{}", bizExcp.getClass().getSimpleName(),
                            module, className, methodName,
                            point.getArgs(),
                            bizExcp.getMessage(), bizException.getContext(),
                            bizException);
                }
                return handleIBizException(bizException, returnType);
            } else if (bizExcp instanceof Exception) {
                log.error("Exception 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        point.getArgs(),
                        bizExcp);
                return handleExceptionDefault(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), returnType);
            } else {
                log.error("Throwable 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        point.getArgs(),
                        bizExcp);
                return handleExceptionDefault(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), returnType);
            }
        } catch (Exception exception) {
            log.error("业务处理逻辑异常：", bizExcp);
            log.error("异常处理逻辑异常", exception);
            return handleExceptionDefault(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), returnType);
        }
    }

    // 异常默认处理
    private Object handleExceptionDefault(int errorCode, String errorMessage, Class returnType) {

        if (Response.class == returnType) {
            Response response = Response.fail(errorCode, errorMessage);
            response.addContext("traceId", TraceIdUtil.traceId());
            return response;
        }

        return handlePrimitiveType(returnType);
    }

    // 业务异常处理
    private Object handleIBizException(IBizException ex, Class returnType) {
        if (Response.class == returnType) {
            Response response = Response.fail(ex.getErrorCode(), ex.getMessage());
            response.addContext("traceId", TraceIdUtil.traceId());
            response.addContext(ex.getContext());
            return response;
        }

        return handlePrimitiveType(returnType);
    }

    // 处理基本类型
    private Object handlePrimitiveType(Class<?> returnType) {
        return ClassUtil.returnBaseDataType(returnType);
    }

}
