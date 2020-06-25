package com.common.collect.lib.util.spring.aop;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.enums.CodeEnum;
import com.common.collect.lib.api.excps.IBizException;
import com.common.collect.lib.util.ClassUtil;
import com.common.collect.lib.util.TraceIdUtil;
import com.common.collect.lib.util.spring.aop.base.DiyAround;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2020/6/25.
 */

@Slf4j
public class CatchExcpAround implements DiyAround.IDiyAround {

    @Override
    public Object doFallback(DiyAround.DiyAroundContext diyAroundContext, Throwable bizExcp, DiyAround diyAround) {
        String module = diyAround.module();
        String className = diyAroundContext.getClassName();
        String methodName = diyAroundContext.getMethodName();
        Class returnType = diyAroundContext.getRetType();
        Object[] args = diyAroundContext.getArgs();
        try {
            if (bizExcp instanceof IBizException) {
                IBizException bizException = (IBizException) bizExcp;
                if (bizExcp.getCause() != null) {
                    log.error("{} 异常。模块：{}，类名：{}，方法：{}，入参：{}，描述:{}, 上下文:{}", bizExcp.getClass().getSimpleName(),
                            module, className, methodName,
                            args,
                            bizExcp.getMessage(), bizException.getContext(),
                            bizException);
                }
                return handleIBizException(bizException, returnType);
            } else if (bizExcp instanceof Exception) {
                log.error("Exception 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        args,
                        bizExcp);
                return handleExceptionDefault(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), returnType);
            } else {
                log.error("Throwable 异常。模块：{}，类名：{}，方法：{},入参：{}",
                        module, className, methodName,
                        args,
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
