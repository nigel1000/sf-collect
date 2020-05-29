package com.common.collect.test.debug.netty.protocol.base;

import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.IdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

// 消息实体转换为字节流
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) {
        // 这里会判断消息类型是不是EMPTY类型，如果是EMPTY类型，则表示当前消息不需要写入到管道中
        if (message.getMessageType() != MessageTypeEnum.EMPTY) {
            out.writeInt(ProtocolConstants.MAGIC_NUMBER);    // 写入当前的魔数
            out.writeByte(ProtocolConstants.MAIN_VERSION);    // 写入当前的主版本号
            out.writeByte(ProtocolConstants.SUB_VERSION);    // 写入当前的次版本号
            out.writeByte(ProtocolConstants.MODIFY_VERSION);    // 写入当前的修订版本号
            if (EmptyUtil.isEmpty(message.getSessionId())) {
                // 生成一个sessionId，并将其写入到字节序列中
                String sessionId = IdUtil.uuidHex();
                message.setSessionId(sessionId);
                out.writeCharSequence(sessionId, StandardCharsets.UTF_8);
            }
            out.writeByte(message.getMessageType().getType());    // 写入当前消息的类型
            if (message.getAttachments() != null) {
                out.writeShort(message.getAttachments().size());    // 写入当前消息的附加参数数量
                message.getAttachments().forEach((key, value) -> {
                    out.writeInt(key.length());    // 写入键的长度
                    out.writeCharSequence(key, StandardCharsets.UTF_8);    // 写入键数据
                    out.writeInt(value.length());    // 希尔值的长度
                    out.writeCharSequence(value, StandardCharsets.UTF_8);    // 写入值数据
                });
            } else {
                out.writeShort(0);    // 写入当前消息的附加参数数量
            }

            if (null == message.getBody()) {
                out.writeInt(0);    // 如果消息体为空，则写入0，表示消息体长度为0
            } else {
                out.writeInt(message.getBody().length());
                out.writeCharSequence(message.getBody(), StandardCharsets.UTF_8);
            }
        }
    }
}