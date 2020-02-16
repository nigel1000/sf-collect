package com.common.collect.test.main.mode.behave;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/11/1.
 * <p>
 * 每一个对象对其下家对引用连接起来形成一条链，请求在此链上传递
 * 降低发出请求的对象和处理请求对象之间的耦合，发出的对象无须知道处理此请求的对象
 * 责任链模式：特定的处理对象对请求或命令的处理变得不确定
 * 命令模式：特定的处理对象对一个命令对执行变得确定
 */

@Slf4j
public class ResponsibilityChain {

    public static void main(String[] args) {
        Handler level1Leader = new Level1Leader(null);
        Handler handler =
                new Level2Leader(
                        new Level3Leader(
                                new Level4Leader(
                                        level1Leader)));
        level1Leader.setNext(handler);

        log.info("#########################");
        handler.handle(new Request(4));

        log.info("#########################");
        handler.handle(new Request(3));

    }
}

class Request {

    @Setter
    @Getter
    private int state;

    Request(int state) {
        if (state > 4 || state <= 0) {
            throw new RuntimeException("状态必须在 1-4");
        }
        this.state = state;
    }
}


abstract class Handler {

    Handler next;

    Handler(Handler next) {
        this.next = next;
    }

    void setNext(Handler next) {
        this.next = next;
    }

    abstract void handle(Request request);

}

@Slf4j
class Level1Leader extends Handler {

    Level1Leader(Handler next) {
        super(next);
    }

    @Override
    public void handle(Request request) {
        if (request.getState() == 0) {
            return;
        }
        if (request.getState() == 1) {
            request.setState(request.getState() - 1);
            log.info("通过 Level1Leader 审核");
        }
        this.next.handle(request);
    }
}

@Slf4j
class Level2Leader extends Handler {

    Level2Leader(Handler next) {
        super(next);
    }

    @Override
    public void handle(Request request) {
        if (request.getState() == 0) {
            return;
        }
        if (request.getState() == 2) {
            request.setState(request.getState() - 1);
            log.info("通过 Level2Leader 审核");
        }
        this.next.handle(request);
    }
}

@Slf4j
class Level3Leader extends Handler {

    Level3Leader(Handler next) {
        super(next);
    }

    @Override
    public void handle(Request request) {
        if (request.getState() == 0) {
            return;
        }
        if (request.getState() == 3) {
            request.setState(request.getState() - 1);
            log.info("通过 Level3Leader 审核");
        }
        this.next.handle(request);
    }
}

@Slf4j
class Level4Leader extends Handler {

    Level4Leader(Handler next) {
        super(next);
    }

    @Override
    public void handle(Request request) {
        if (request.getState() == 0) {
            return;
        }
        if (request.getState() == 4) {
            request.setState(request.getState() - 1);
            log.info("通过 Level4Leader 审核");
        }
        this.next.handle(request);
    }
}




