package com.common.collect.container.docs;

import com.common.collect.api.excps.UnifiedException;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Data
public class DocsMethodConfig implements Serializable {

    private MethodParamType methodParamType;
    // REQUEST_PARAM
    private List<DocsMethodParamConfig> requestParams = new ArrayList<>();
    //REQUEST_BODY
    private Object requestBody;

    private Map<String, Object> responseBody = new LinkedHashMap<>();

    public void valid() {
        if (methodParamType == null) {
            throw UnifiedException.gen("methodParamType 不能为空");
        }
    }

    public void setRequestParams(List<DocsMethodParamConfig> requestParams) {
        this.requestParams.addAll(requestParams);
    }

    public void addRequestParams(DocsMethodParamConfig requestParam) {
        this.requestParams.add(requestParam);
    }

    public void setResponseBody(Map<String, Object> responseBody) {
        this.responseBody.putAll(responseBody);
    }

    public void putResponseBody(String key, Object value) {
        this.responseBody.put(key, value);
    }

}
