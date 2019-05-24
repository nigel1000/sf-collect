package com.common.collect.container.trace.filter.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.common.collect.container.trace.TraceConstants;
import com.common.collect.container.trace.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Activate(group = {Constants.CONSUMER})
public class SetTraceIdFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            RpcContext context = RpcContext.getContext();
            if (context.isConsumerSide()) {
                context.setAttachment(TraceConstants.TRACE_ID_KEY,
                        TraceIdUtil.initTraceIdIfAbsent());
            }
        } catch (Exception ex) {
            log.info("SetTraceIdFilter exception:", ex);
        } finally {
            TraceIdUtil.clearTraceId();
        }
        return invoker.invoke(invocation);
    }

}