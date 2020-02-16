package com.common.collect.test.main.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 抽象核心流程，不同的子类实现剩余的逻辑
 */
public class Template {

    public static void main(String[] args) {
        HttpServlet httpServlet = new HttpServlet();
        httpServlet.service();
    }

}

@Slf4j
abstract class Servlet {

    void service() {
        doInit();
        doHandle();
        doDestroy();
    }

    // 抽象方法
    abstract void doInit();

    // 具体方法
    private void doDestroy() {
        log.info("Servlet doDestroy");
    }

    // 钩子方法 空实现
    void doHandle() {
    }

}

@Slf4j
class HttpServlet extends Servlet {
    @Override
    void doInit() {
        log.info("HttpServlet doInit");
    }

    @Override
    void doHandle() {
        log.info("HttpServlet doHandle");
    }
}