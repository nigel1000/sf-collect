package com.common.collect.lib.util.framework.trace;


import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.TraceIdUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nijianfeng on 2018/10/5.
 */
public class TraceIdRequestUtil {

    public static void initTraceId(HttpServletRequest request) {
        String contextTraceId = request.getHeader(TraceIdUtil.TRACE_ID_KEY);
        if (EmptyUtil.isBlank(contextTraceId)) {
            contextTraceId = request.getParameter(TraceIdUtil.TRACE_ID_KEY);
        }
        TraceIdUtil.initTraceId(contextTraceId);
    }

}
