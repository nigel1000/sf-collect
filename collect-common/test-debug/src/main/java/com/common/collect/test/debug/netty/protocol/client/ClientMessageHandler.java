package com.common.collect.test.debug.netty.protocol.client;

import com.common.collect.test.debug.netty.protocol.base.Message;
import com.common.collect.test.debug.netty.protocol.base.MessageResolver;
import com.common.collect.test.debug.netty.protocol.base.MessageResolverFactory;
import com.common.collect.test.debug.netty.protocol.base.MessageTypeEnum;
import com.common.collect.test.debug.netty.protocol.base.resolver.PingMessageResolver;
import com.common.collect.test.debug.netty.protocol.base.resolver.PongMessageResolver;
import com.common.collect.test.debug.netty.protocol.base.resolver.RequestMessageResolver;
import com.common.collect.test.debug.netty.protocol.base.resolver.ResponseMessageResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// 客户端消息处理器
public class ClientMessageHandler extends SimpleChannelInboundHandler<Message> {

    // 创建一个线程，模拟用户发送消息
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    // 获取一个消息处理器工厂类实例
    private MessageResolverFactory resolverFactory = MessageResolverFactory.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        MessageResolver resolver = resolverFactory.getMessageResolver(message);    // 获取消息处理器
        Message result = resolver.resolve(message);    // 对消息进行处理并获取响应数据
        ctx.writeAndFlush(result);    // 将响应数据写入到处理器中
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        resolverFactory.registerResolver(new RequestMessageResolver());    // 注册request消息处理器
        resolverFactory.registerResolver(new ResponseMessageResolver());// 注册response消息处理器
        resolverFactory.registerResolver(new PingMessageResolver());    // 注册ping消息处理器
        resolverFactory.registerResolver(new PongMessageResolver());    // 注册pong消息处理器
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 对于客户端，在建立连接之后，在一个独立线程中模拟用户发送数据给服务端
        executor.execute(new MessageSender(ctx));
    }

    private static final class MessageSender implements Runnable {

        private static final AtomicLong counter = new AtomicLong(1);
        private volatile ChannelHandlerContext ctx;

        public MessageSender(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try {
                while (counter.get() < 6) {
                    // 模拟随机发送消息的过程
                    int time = new Random().nextInt(6);
                    TimeUnit.SECONDS.sleep(time);
                    if (time > 3) {
                        Message message = new Message();
                        message.setMessageType(MessageTypeEnum.PING);
                        ctx.writeAndFlush(message);
                    } else {
                        Message message = new Message();
                        message.setMessageType(MessageTypeEnum.REQUEST);
                        message.setBody("this is my " + counter.getAndIncrement() + " message.");
                        message.addAttachment("name", "client");
                        ctx.writeAndFlush(message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}