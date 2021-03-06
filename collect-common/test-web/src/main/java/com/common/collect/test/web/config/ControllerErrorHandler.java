package com.common.collect.test.web.config;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.ThreadLocalUtil;
import com.common.collect.lib.util.TraceIdUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nijianfeng on 2018/8/14.
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ControllerErrorHandler {

    private static final String PARAM_ERROR_MESSAGE = "输入参数不合法";
    private static final String INTERNAL_EXCEPTION = "发生未知错误，请联系技术人员排查!";

    @Data
    private static class RtnParam {

        private Integer errorCode;

        private String errorMsg;

        private Map<String, Object> context;

        public RtnParam(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public RtnParam(int errorCode, String errorMsg, Map<String, Object> context) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
            this.context = context;
        }
    }

    private Object getResponse(@NonNull RtnParam rtnParam) {
        Class<?> rtnType = ThreadLocalUtil.pull(AjaxInterceptor.ajax_rtn_type);

        Response defResponse = Response.fail(rtnParam.getErrorMsg());
        if (rtnParam.getErrorCode() != null) {
            defResponse = Response.fail(rtnParam.getErrorCode(), rtnParam.getErrorMsg());
        }
        defResponse.addContext("traceId", TraceIdUtil.traceId());
        defResponse.addContext(rtnParam.getContext());

        if (Response.class.equals(rtnType)) {
            return defResponse;
        } else {
            return defResponse;
        }
    }

    /**
     * Valid标签 校验失败
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, BindException ex) {
        printLogInfo(request, ex);
        List<FieldError> fieldErrors = ex.getFieldErrors();
        if (EmptyUtil.isEmpty(fieldErrors)) {
            return getResponse(new RtnParam(PARAM_ERROR_MESSAGE));
        }
        FieldError fieldError = fieldErrors.get(0);
        return getResponse(new RtnParam(fieldError.getDefaultMessage()));
    }

    /**
     * Validated标签 校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, ConstraintViolationException ex) {
        printLogInfo(request, ex);
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (EmptyUtil.isEmpty(constraintViolations)) {
            return getResponse(new RtnParam(PARAM_ERROR_MESSAGE));
        }
        ConstraintViolation<?> next = constraintViolations.iterator().next();
        return getResponse(new RtnParam(next.getMessage()));
    }

    /**
     * required=true 校验失败
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, MissingServletRequestParameterException ex) {
        printLogInfo(request, ex);
        return getResponse(new RtnParam(ex.getMessage()));
    }

    /**
     * JSR303注解参数 校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processJsr303ValidatorError(NativeWebRequest request, MethodArgumentNotValidException ex) {
        printLogInfo(request, ex);
        BindingResult errors = ex.getBindingResult();
        if (EmptyUtil.isEmpty(errors.getFieldErrors())) {
            return getResponse(new RtnParam(PARAM_ERROR_MESSAGE));
        }
        FieldError fieldError = errors.getFieldErrors().get(0);
        return getResponse(new RtnParam(fieldError.getDefaultMessage()));
    }

    /**
     * 输入参数数据类型转换错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, HttpMessageNotReadableException ex) {
        printLogInfo(request, ex);
        return getResponse(new RtnParam(PARAM_ERROR_MESSAGE));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(UnifiedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, UnifiedException ex) {
        String message = ex.getMessage();
        if (ex.getCause() != null) {
            printLogInfo(request, ex);
        }
        return getResponse(new RtnParam(ex.getErrorCode(), message, ex.getContext()));
    }

    /**
     * 兜底
     */
    @ExceptionHandler({RuntimeException.class, Exception.class, Error.class, Throwable.class})
    @ResponseStatus(HttpStatus.OK)
    public Object processControllerError(NativeWebRequest request, RuntimeException ex) {
        printLogInfo(request, ex);
        return getResponse(new RtnParam(INTERNAL_EXCEPTION));
    }

    /**
     * 打印日志信息
     */
    private void printLogInfo(NativeWebRequest request, Throwable ex) {
        log.info("request parameters:{}", request.getParameterMap(), ex);
    }
}