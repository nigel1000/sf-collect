package com.common.collect.container;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

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
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
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

}
