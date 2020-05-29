package com.common.collect.test.debug.netty.protocol.base;

import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class Message {
    // 魔数 4个字节 当前字节序列是当前类型的协议
    private int magicNumber;
    // 主版本号 1个字节
    private byte mainVersion;
    // 次版本号 1个字节
    private byte subVersion;
    // 修订版本号 1个字节
    private byte modifyVersion;
    // 会话id 8个字节 用于串联请求和响应
    private String sessionId;
    // 消息类型 1个字节
    private MessageTypeEnum messageType;
    // 附加数据
    private Map<String, String> attachments;
    // 消息体
    private String body;

    public Map<String, String> unmodifiableAttachments() {
        return Collections.unmodifiableMap(attachments);
    }

    public void addAttachments(Map<String, String> attachments) {
        if (null != attachments) {
            this.attachments.putAll(attachments);
        }
    }

    public void addAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        attachments.put(key, value);
    }

}