package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.io.InputStream;
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

    public static InputStream get(@NonNull String url) {
        Request request = new Request.Builder().url(url).build();
        okhttp3.Response response;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw UnifiedException.gen("download url failed!" + url);
            }
            return response.body().byteStream();
        } catch (IOException ex) {
            throw UnifiedException.gen("download url failed!" + url, ex);
        }
    }

}
