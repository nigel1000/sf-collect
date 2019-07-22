package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.AlgorithmUtil;
import lombok.NonNull;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * Created by hznijianfeng on 2019/3/21.
 */

public class WebUtil {

    // 导出文件
    public static void exportHttpServletResponse(@NonNull HttpServletResponse response,
                                                 @NonNull byte[] data,
                                                 @NonNull String fileName) {
        try {
            // 将输出流写出到servlet
            response.setContentLength(data.length);
            // url编码
            setHeaderDownload(response, AlgorithmUtil.uRLEncoderUtf8(fileName));
            response.setContentType("application/octet-stream");
            OutputStream servletOutPutStream = response.getOutputStream();
            servletOutPutStream.write(data);
            // 刷新servlet输出流
            servletOutPutStream.flush();
            servletOutPutStream.close();
        } catch (Exception ex) {
            throw UnifiedException.gen("导出 HttpServletResponse 异常", ex);
        }
    }

    public static void setHeaderDownload(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    }

    public static void setHeaderLastModified(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader("Last-Modified", lastModifiedDate);
    }

    public static void setHeaderEtag(HttpServletResponse response, String etag) {
        response.setHeader("ETag", etag);
    }

    public static void setHeaderExpires(HttpServletResponse response, long expiresSeconds) {
        response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000L);
        response.setHeader("Cache-Control", "max-age=" + expiresSeconds);
    }

    public static void setHeaderNoCache(HttpServletResponse response) {
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-cache");
    }


}
