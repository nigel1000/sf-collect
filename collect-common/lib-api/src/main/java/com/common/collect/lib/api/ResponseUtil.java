package com.common.collect.lib.api;


import com.common.collect.lib.api.enums.CodeEnum;
import com.common.collect.lib.api.excps.UnifiedException;

/**
 * Created by nijianfeng on 2018/8/14.
 */

public class ResponseUtil {


    public static <T> T parse(Response<T> response) {
        UnifiedException exception;
        if (response == null) {
            exception = UnifiedException.gen(CodeEnum.RPC_INVOKE_ERROR.getMsg());
            exception.addContext("error_type", "返回为 null");
            throw exception;
        }
        if (response.isSuccess()) {
            return response.getResult();
        }
        if (response.getError() != null) {
            exception = UnifiedException.gen(String.valueOf(response.getError()));
            exception.addContext(response.getContext());
            throw exception;
        }
        exception = UnifiedException.gen(CodeEnum.RPC_INVOKE_ERROR.getMsg());
        exception.addContext(response.getContext());
        throw exception;
    }

}
