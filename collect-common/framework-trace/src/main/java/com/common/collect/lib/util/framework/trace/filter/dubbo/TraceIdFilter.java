package com.common.collect.lib.util.framework.trace.filter.dubbo;

import com.alibaba.dubbo.rpc.*;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.TraceIdUtil;

/**
 * Created by nijianfeng on 2018/10/5.
 */
public class TraceIdFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = RpcContext.getContext().getAttachment(TraceIdUtil.TRACE_ID_KEY);
        if (EmptyUtil.isBlank(traceId)) {
            RpcContext.getContext().setAttachment(TraceIdUtil.TRACE_ID_KEY, TraceIdUtil.initTraceId(TraceIdUtil.traceId()));
        } else {
            TraceIdUtil.initTraceId(traceId);
        }
        return invoker.invoke(invocation);
    }


//    @Activate(group = {Constants.CONSUMER})
//    public class SetTraceIdFilter implements Filter {
//
//        @Override
//        public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//            RpcContext context = RpcContext.getContext();
//            if (context.isConsumerSide()) {
//                context.setAttachment(TraceIdUtil.TRACE_ID_KEY,
//                        TraceIdUtil.initTraceId(TraceIdUtil.traceId()));
//            }
//            return invoker.invoke(invocation);
//        }
//
//    }
//
//    @Activate(group = {Constants.PROVIDER})
//    public class GetTraceIdFilter implements Filter {
//
//        @Override
//        public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//            RpcContext context = RpcContext.getContext();
//            if (context.isProviderSide()) {
//                TraceIdUtil.initTraceId(context.getAttachment(TraceIdUtil.TRACE_ID_KEY));
//            }
//            return invoker.invoke(invocation);
//        }
//
//    }

}
