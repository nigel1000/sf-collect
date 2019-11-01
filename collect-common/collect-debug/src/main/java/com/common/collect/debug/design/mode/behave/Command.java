package com.common.collect.debug.design.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/11/1.
 * <p>
 * 把一个请求或者操作封装到一个对象中
 * 把发出命令的责任(命令发起者)和执行命令(命令接收者)的责任分开
 */
public class Command {

    // 客户端
    public static void main(String[] args) {
        TV tv = new TV();
        Control control = new Control(
                new OnOrder(tv)
                , new OffOrder(tv)
                , new ChannelUpOrder(tv)
                , new ChannelDownOrder(tv));

        control.on();
        control.channnelUp();
        control.channelDown();
        control.off();
    }

}

// 命令发起者 遥控板
class Control {

    private Order on;
    private Order off;
    private Order channelUp;
    private Order channelDown;

    Control(Order on, Order off, Order channelUp, Order channelDown) {
        this.on = on;
        this.off = off;
        this.channelUp = channelUp;
        this.channelDown = channelDown;
    }

    void on() {
        on.execute();
    }

    void off() {
        off.execute();
    }

    void channnelUp() {
        channelUp.execute();
    }

    void channelDown() {
        channelDown.execute();
    }
}

// 命令
abstract class Order {

    TV tv;

    Order(TV tv) {
        this.tv = tv;
    }

    abstract void execute();
}

// 关
class OffOrder extends Order {
    OffOrder(TV tv) {
        super(tv);
    }

    @Override
    void execute() {
        tv.off();
    }
}

// 开
class OnOrder extends Order {
    OnOrder(TV tv) {
        super(tv);
    }

    @Override
    void execute() {
        tv.on();
    }
}

// 上一频道
class ChannelUpOrder extends Order {
    ChannelUpOrder(TV tv) {
        super(tv);
    }

    @Override
    void execute() {
        tv.channelUp();
    }
}

// 下一频道
class ChannelDownOrder extends Order {
    ChannelDownOrder(TV tv) {
        super(tv);
    }

    @Override
    void execute() {
        tv.channelDown();
    }
}

// 命令接收者
@Slf4j
class TV {

    void off() {
        log.info("tv off");
    }

    void on() {
        log.info("tv on");
    }

    void channelUp() {
        log.info("tv channel up");
    }

    void channelDown() {
        log.info("tv channel down");
    }

}



