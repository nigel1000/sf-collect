package com.common.collect.api.excps;

import com.common.collect.api.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hznijianfeng on 2020/1/13.
 */

public interface IBizException {

    default int getErrorCode() {
        return Constants.ERROR;
    }

    default String getErrorMessage() {
        return Constants.errorFromSystem;
    }

    default Map<String, Object> getContext() {
        return new HashMap<>();
    }

}
