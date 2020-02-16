package com.common.collect.lib.api.excps;

import com.common.collect.lib.api.enums.CodeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2020/1/13.
 */

public interface IBizException {

    default int getErrorCode() {
        return CodeEnum.FAIL.getCode();
    }

    default String getMessage() {
        return CodeEnum.FAIL.getMsg();
    }

    default Map<String, Object> getContext() {
        return new HashMap<>();
    }

}
