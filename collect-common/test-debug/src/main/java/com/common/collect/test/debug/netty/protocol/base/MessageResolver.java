package com.common.collect.test.debug.netty.protocol.base;

/**
 * Created by hznijianfeng on 2020/5/29.
 */

public interface MessageResolver {

    boolean support(Message message);

    Message resolve(Message message);
}
