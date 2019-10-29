package com.common.collect.debug.design.mode.create;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/29.
 * <p>
 * 产品的内部零件构造(builder 构造者)与产品的零件组装(director 导演类)对客户端隐藏
 */
public class Builder {

    public static void main(String[] args) {
        new SMSMessageBuilder()
                .from("15088667654")
                .to("15089765643")
                .content("这是短信")
                .build()
                .send();

        new EmailMessageBuilder()
                .from("15088667654@163.com")
                .to("15089765643@163.com")
                .content("这是邮件")
                .build()
                .send();
    }

}

@Data
abstract class Message {

    private String from;
    private String to;
    private String content;

    abstract public void send();

}

@Slf4j
class SMSMessage extends Message {

    @Override
    public void send() {
        log.info("SMSMessage send:{}", this);
    }
}


@Slf4j
class EmailMessage extends Message {

    @Override
    public void send() {
        log.info("EmailMessage send:{}", this);
    }

}

abstract class MessageBuilder {

    Message message;

    MessageBuilder from(String from) {
        message.setFrom(from);
        return this;
    }

    MessageBuilder to(String to) {
        message.setTo(to);
        return this;
    }

    MessageBuilder content(String content) {
        message.setContent(content);
        return this;
    }

    Message build() {
        return message;
    }

}

@Slf4j
class SMSMessageBuilder extends MessageBuilder {

    SMSMessageBuilder() {
        message = new SMSMessage();
    }

    @Override
    Message build() {
        log.info("SMSMessageBuilder build:{}", message);
        return super.build();
    }
}

@Slf4j
class EmailMessageBuilder extends MessageBuilder {
    EmailMessageBuilder() {
        message = new EmailMessage();
    }

    @Override
    Message build() {
        log.info("EmailMessageBuilder build:{}", message);
        return super.build();
    }
}