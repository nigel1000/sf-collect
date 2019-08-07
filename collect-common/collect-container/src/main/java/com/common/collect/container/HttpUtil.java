package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import com.common.collect.util.StringUtil;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hznijianfeng on 2019/3/6.
 */

@Slf4j
public class HttpUtil {

    private final static OkHttpClient.Builder HTTP_CLIENT_BUILDER = new OkHttpClient.Builder();
    private final static OkHttpClient HTTP_CLIENT =
            HTTP_CLIENT_BUILDER.connectionPool(new ConnectionPool()).connectTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();

    @Data
    public static class HttpParam implements Serializable {

        private String url;
        private Map<String, String> headers;
        private RequestEnum requestType;
        // PostRequestBody 使用
        private String contentType = "application/json;charset=UTF-8";
        private String content;
        // PostFormBody 使用
        private Map<String, String> params;
        // 异步操作
        private Callback callback;

        public void validSelf() {
            if (EmptyUtil.isBlank(url)) {
                throw UnifiedException.gen("url 不能为空");
            }

            if (requestType == null) {
                throw UnifiedException.gen("requestType 不能为空");
            }
        }

        private enum RequestEnum {
            Get,
            PostFormBody,
            PostRequestBody
        }

        public HttpParam(String url) {
            this.url = url;
            this.requestType = RequestEnum.Get;
        }

        public HttpParam(String url, String content, String contentType) {
            this.url = url;
            this.content = content;
            if (contentType != null) {
                this.contentType = contentType;
            }
            this.requestType = RequestEnum.PostRequestBody;
        }

        public HttpParam(String url, Map<String, String> params) {
            this.url = url;
            this.params = params;
            this.requestType = RequestEnum.PostFormBody;
        }

    }

    public static InputStream getInputStream(@NonNull String url) {
        return request(new HttpParam(url), InputStream.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T request(@NonNull HttpParam httpParam, Class<T> ret) {
        httpParam.validSelf();

        String url = httpParam.getUrl();

        // 入参
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url);

        Map<String, String> headers = httpParam.getHeaders();
        if (EmptyUtil.isNotEmpty(headers)) {
            headers.forEach(reqBuilder::header);
        }

        Map<String, String> params = httpParam.getParams();
        if (HttpParam.RequestEnum.PostFormBody.equals(httpParam.getRequestType()) && EmptyUtil.isNotEmpty(params)) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            params.forEach(formBuilder::add);
            reqBuilder.post(formBuilder.build());
        }

        if (HttpParam.RequestEnum.PostRequestBody.equals(httpParam.getRequestType())) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(httpParam.getContentType()), httpParam.getContent());
            reqBuilder.post(requestBody);
        }

        Request request = reqBuilder.build();

        // 访问
        okhttp3.Response response;
        try {
            if (httpParam.getCallback() == null) {
                response = HTTP_CLIENT.newCall(request).execute();
            } else {
                // 异步执行
                HTTP_CLIENT.newCall(request).enqueue(httpParam.getCallback());
                return null;
            }
        } catch (IOException e) {
            throw UnifiedException.gen(StringUtil.format("url:{} 访问异常", url), e);
        }
        if (response == null) {
            throw UnifiedException.gen(StringUtil.format("url:{} 数据异常, response 为空", url));
        }
        ResponseBody body = response.body();
        if (body == null) {
            throw UnifiedException.gen(StringUtil.format("url:{} 数据异常, response:{}, body 为空", url, response));
        }

        // 返回
        if (String.class.equals(ret)) {
            try {
                return (T) body.string();
            } catch (IOException e) {
                throw UnifiedException.gen(StringUtil.format("{} 返回异常", url), e);
            }
        } else if (InputStream.class.equals(ret)) {
            return (T) body.byteStream();
        } else if (byte[].class.equals(ret)) {
            try {
                return (T) body.bytes();
            } catch (IOException e) {
                throw UnifiedException.gen(StringUtil.format("{} 返回异常", url), e);
            }
        } else if (ResponseBody.class.equals(ret)) {
            return (T) body;
        } else if (okhttp3.Response.class.equals(ret)) {
            return (T) response;
        } else {
            throw UnifiedException.gen("返回类型 不合法");
        }

    }
}
