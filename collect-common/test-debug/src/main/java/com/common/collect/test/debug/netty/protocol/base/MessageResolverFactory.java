package com.common.collect.test.debug.netty.protocol.base;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MessageResolverFactory {

  // 创建一个工厂类实例
  private static final MessageResolverFactory resolverFactory = new MessageResolverFactory();
  private static final List<MessageResolver> resolvers = new CopyOnWriteArrayList<>();

  private MessageResolverFactory() {}

  // 使用单例模式实例化当前工厂类实例
  public static MessageResolverFactory getInstance() {
    return resolverFactory;
  }

  public void registerResolver(MessageResolver resolver) {
    resolvers.add(resolver);
  }

  // 根据解码后的消息，在工厂类处理器中查找可以处理当前消息的处理器
  public MessageResolver getMessageResolver(Message message) {
    for (MessageResolver resolver : resolvers) {
      if (resolver.support(message)) {
        return resolver;
      }
    }
    throw new RuntimeException("cannot find resolver, message type: " + message.getMessageType());
  }

}