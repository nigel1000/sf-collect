package com.common.collect.test.debug.netty.protocol.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageTypeEnum {

    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    PING((byte) 3),
    PONG((byte) 4),
    // 该消息不会写入数据管道
    EMPTY((byte) 5),
    ;

    private byte type;

    public static MessageTypeEnum from(byte type) {
        for (MessageTypeEnum value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new RuntimeException("unsupported type: " + type);
    }

}