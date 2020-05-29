package com.common.collect.test.debug.netty.protocol.base.resolver;

import com.common.collect.test.debug.netty.protocol.base.Message;
import com.common.collect.test.debug.netty.protocol.base.MessageResolver;
import com.common.collect.test.debug.netty.protocol.base.MessageTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

// request类型的消息
public class RequestMessageResolver implements MessageResolver {

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public boolean support(Message message) {
        return message.getMessageType() == MessageTypeEnum.REQUEST;
    }

    @Override
    public Message resolve(Message message) {
        // 接收到request消息之后，对消息进行处理，这里主要是将其打印出来
        int index = counter.getAndIncrement();
        System.out.println("[sessionId: " + message.getSessionId() + "]"
                + index + ". receive request: " + message.getBody());
        System.out.println("[sessionId: " + message.getSessionId() + "]"
                + index + ". attachments: " + message.getAttachments());

        // 处理完成后，生成一个响应消息返回
        Message response = new Message();
        response.setMessageType(MessageTypeEnum.RESPONSE);
        response.setBody("nice to meet you too!");
        response.addAttachment("param", "{\"id\":1}");
        response.addAttachment("return", "{\"name\":server}");
        return response;
    }
}