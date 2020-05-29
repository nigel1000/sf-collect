package com.common.collect.test.debug.netty.protocol.base.resolver;

import com.common.collect.test.debug.netty.protocol.base.Message;
import com.common.collect.test.debug.netty.protocol.base.MessageResolver;
import com.common.collect.test.debug.netty.protocol.base.MessageTypeEnum;

// ping消息处理器
public class PingMessageResolver implements MessageResolver {

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.PING;
    }

    @Override
    public Message resolve(Message message) {
        // 接收到ping消息后，返回一个pong消息返回
        System.out.println("receive ping message: " + System.currentTimeMillis());
        Message pong = new Message();
        pong.setMessageType(MessageTypeEnum.PONG);
        return pong;
    }
}