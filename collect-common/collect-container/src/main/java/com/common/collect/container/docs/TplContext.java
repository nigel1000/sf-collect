package com.common.collect.container.docs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.JsonUtil;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.PathUtil;
import com.common.collect.util.SplitUtil;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/5/20.
 */
@Data
public class TplContext {

    private String methodParamType;
    // REQUEST_PARAM
    private List<DocsMethodParamConfig> requestParams = new ArrayList<>();
    //REQUEST_BODY
    private String requestBody;
    private String requestBodyComment;

    private Map<String, String> responseBody;
    private Map<String, String> responseBodyComment;


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
    private boolean showComment;
    // 支持 http 访问类型
    private String supportRequest;

    public static TplContext build(
            @NonNull DocsGlobalConfig globalConfig,
            DocsApi docsApi,
            @NonNull DocsApiMethod docsApiMethod,
            DocsMethodConfig docsMethodConfig) {

        TplContext tplContext = new TplContext();
        String rootDirName = "";
        String urlPrefix = "";
        if (docsApi != null) {
            rootDirName = docsApi.rootDirName();
            urlPrefix = docsApi.urlPrefix();
        }
        tplContext.setSavePath(PathUtil.correctSeparator(globalConfig.getPrefixPath() + File.separator + rootDirName + File.separator + docsApiMethod.nodeName()));
        tplContext.setRequestUrl(urlPrefix + docsApiMethod.urlSuffix());
        tplContext.setMethodAuthor(docsApiMethod.methodAuthor());
        tplContext.setMethodDesc(docsApiMethod.methodDesc());
        tplContext.setReCreate(priorityConfig(globalConfig.isReCreate(), docsApiMethod.reCreate(), docsApi != null && docsApi.reCreate()));
        tplContext.setShowComment(priorityConfig(globalConfig.isShowComment(), docsApiMethod.showComment(), docsApi != null && docsApi.showComment()));
        tplContext.setSupportRequest(SplitUtil.join(Arrays.asList(docsApiMethod.supportRequest()), " | "));

        docsMethodConfig.valid();
        tplContext.setMethodParamType(docsMethodConfig.getMethodParamType().name());
        tplContext.setRequestParams(docsMethodConfig.getRequestParams());
        tplContext.setRequestBody(JsonUtil.bean2jsonPretty(docsMethodConfig.getRequestBody()));
        if (tplContext.isShowComment()) {
            tplContext.setRequestBodyComment(bean2jsonPretty(docsMethodConfig.getRequestBody()));
        }

        Map<String, Object> responseBody = docsMethodConfig.getResponseBody();
        Map<String, String> responseBodyStr = new LinkedHashMap<>();
        for (String key : responseBody.keySet()) {
            responseBodyStr.put(key, JsonUtil.bean2jsonPretty(responseBody.get(key)));
        }
        tplContext.setResponseBody(responseBodyStr);

        if (tplContext.isShowComment()) {
            Map<String, String> responseBodyCommentStr = new LinkedHashMap<>();
            for (String key : responseBody.keySet()) {
                responseBodyCommentStr.put(key, bean2jsonPretty(responseBody.get(key)));
            }
            tplContext.setResponseBodyComment(responseBodyCommentStr);
        }

        tplContext.valid();
        return tplContext;
    }

    public static String bean2jsonPretty(Object bean) {
        SerializerFeature[] serializerFeatures = new SerializerFeature[]{
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect};
        return JSON.toJSONString(bean, new FieldCommentFilter(), serializerFeatures);
    }

    private void valid() {
        if (EmptyUtil.isEmpty(savePath)) {
            throw UnifiedException.gen("savePath 不能为空");
        }
    }

    private static boolean priorityConfig(boolean global, boolean method, boolean cls) {
        if (global) {
            return true;
        } else if (method) {
            return true;
        } else if (cls) {
            return true;
        } else {
            return false;
        }
    }

}
