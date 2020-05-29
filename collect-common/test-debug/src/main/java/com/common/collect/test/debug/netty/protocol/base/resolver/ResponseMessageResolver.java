package com.common.collect.test.debug.netty.protocol.base.resolver;

import com.common.collect.test.debug.netty.protocol.base.Message;
import com.common.collect.test.debug.netty.protocol.base.MessageResolver;
import com.common.collect.test.debug.netty.protocol.base.MessageTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

// 响应消息处理器
public class ResponseMessageResolver implements MessageResolver {

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.RESPONSE;
    }

    @Override
    public Message resolve(Message message) {
        // 接收到对方服务的响应消息之后，对响应消息进行处理，这里主要是将其打印出来
        int index = counter.getAndIncrement();
        System.out.println("[sessionId: " + message.getSessionId() + "]"
                + index + ". receive response: " + message.getBody());
        System.out.println("[sessionId: " + message.getSessionId() + "]"
                + index + ". attachments: " + message.getAttachments());

        // 响应消息不需要向对方服务再发送响应，因而这里写入一个空消息
        Message empty = new Message();
        empty.setMessageType(MessageTypeEnum.EMPTY);
        return empty;
    }
}