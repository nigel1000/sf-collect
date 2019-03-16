package com.common.collect.container.aops;

import com.common.collect.api.Response;
import com.common.collect.api.enums.CommonError;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.AspectUtil;
import com.common.collect.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

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

        CatchExcp catchExcp = AspectUtil.getAnnotation(point, CatchExcp.class);
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
                log.info(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                        point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
                log.error(LogConstant.EXCP_LOG_PREFIX + " excpModule:{}, excpMessage:{}, excpContext:{}", module,
                        className, methodName, ex.getModule(), ex.getErrorMessage(), ex.getContext(), ex);
            }
            return returnResult(ex.getErrorCode(), ex.getErrorMessage(), returnType);
        } catch (DataAccessException | SQLException ex) {
            log.info(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
            log.error(LogConstant.EXCP_LOG_PREFIX, module, className, methodName, ex);
            return returnResult(CommonError.DB_ERROR.getErrorCode(), CommonError.DB_ERROR.getErrorMessage(),
                    returnType);
        } catch (RuntimeException ex) {
            log.error("======================RuntimeException======================");
            log.info(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
            log.error(LogConstant.EXCP_LOG_PREFIX, module, className, methodName, ex);
            return returnResult(CommonError.SYSTEM_ERROR.getErrorCode(), CommonError.SYSTEM_ERROR.getErrorMessage(),
                    returnType);
        } catch (Exception ex) {
            log.error("======================Exception======================");
            log.info(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
            log.error(LogConstant.EXCP_LOG_PREFIX, module, className, methodName, ex);
            return returnResult(CommonError.SYSTEM_ERROR.getErrorCode(), CommonError.SYSTEM_ERROR.getErrorMessage(),
                    returnType);
        } catch (Throwable ex) {
            log.error("======================Throwable======================");
            log.info(LogConstant.START_LOG_PREFIX + " args:{}", module, point.getTarget().getClass().getName(),
                    point.getSignature().getName(), LogConstant.getObjString(point.getArgs()));
            log.error(LogConstant.EXCP_LOG_PREFIX, module, className, methodName, ex);
            return returnResult(CommonError.SYSTEM_ERROR.getErrorCode(), CommonError.SYSTEM_ERROR.getErrorMessage(),
                    returnType);
        }
    }

    private Object returnResult(int errorCode, String errorMessage, Class returnType) {

        if (Response.class == returnType) {
            return Response.fail(errorCode, errorMessage);
        }
        return TypeUtil.returnBaseDataType(returnType);
    }


}
