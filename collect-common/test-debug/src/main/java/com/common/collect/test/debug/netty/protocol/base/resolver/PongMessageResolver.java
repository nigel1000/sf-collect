package com.common.collect.test.debug.netty.protocol.base.resolver;

import com.common.collect.test.debug.netty.protocol.base.Message;
import com.common.collect.test.debug.netty.protocol.base.MessageResolver;
import com.common.collect.test.debug.netty.protocol.base.MessageTypeEnum;

// pong消息处理器
public class PongMessageResolver implements MessageResolver {

  @Override
  public boolean support(Message message) {
    return message.getMessageType() == MessageTypeEnum.PONG;
  }

  @Override
  public Message resolve(Message message) {
    // 接收到pong消息后，不需要进行处理，直接返回一个空的message
    System.out.println("receive pong message: " + System.currentTimeMillis());
    Message empty = new Message();
    empty.setMessageType(MessageTypeEnum.EMPTY);
    return empty;
  }
}