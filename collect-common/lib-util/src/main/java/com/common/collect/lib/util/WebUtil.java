package com.common.collect.lib.util;

import com.common.collect.lib.api.excps.UnifiedException;
import lombok.NonNull;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by hznijianfeng on 2019/3/21.
 */

public class WebUtil {

    // 导出文件
    public static void exportFile(@NonNull HttpServletResponse response,
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

    public static void exportHtml(@NonNull HttpServletResponse response,
                                  @NonNull String content) {
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(content);
            out.flush();
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
