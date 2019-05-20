package com.common.collect.container.docs;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private Object responseBody;


    // 文档保存路径
    private String savePath;
    // http url
    private String requestUrl;
    // 方法作者
    private String methodAuthor;
    // 功能描述
    private String methodDesc;
    // 是否覆盖重写
    private boolean reCreate;
    // 支持 http 访问类型
    private String supportRequest;

    public void valid() {
        if (methodParamType == null) {
            throw UnifiedException.gen("methodParamType 不能为空");
        }
        if (EmptyUtil.isEmpty(savePath)) {
            throw UnifiedException.gen("savePath 不能为空");
        }
    }

    public String getRequestBody() {
        return JsonUtil.bean2jsonPretty(requestBody);
    }

    public Object getResponseBody() {
        return JsonUtil.bean2jsonPretty(responseBody);
    }

    public String getMethodParamType() {
        return methodParamType.name();
    }

}
