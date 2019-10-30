package com.common.collect.debug.design.mode.struct;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Created by nijianfeng on 2019/10/30.
 * <p>
 * 给某一对象提供一个代理对象，由代理对象控制对原对象的引用
 * 对象对引用加以控制(权限控制&计数)，不增强对象本身对功能
 * 保护代理：代理对象对调用者进行权限检查，只通过有适当权限的
 * 智能代理：对调用者进行调用统计记录
 * 虚拟代理：延迟加载，快速相应并在合适的时机使用真实对象加载
 */
public class Proxy {

    public static void main(String[] args) {
        Query query = new GoodsQueryProxy();
        query.doQuery();
    }

}

// 画家
interface Query {
    void doQuery();
}

@Slf4j
class GoodsQuery implements Query {

    @Override
    public void doQuery() {
        log.info("query goods");
    }
}

@Slf4j
class GoodsQueryProxy implements Query {

    private GoodsQuery goodsQuery = new GoodsQuery();

    @Override
    public void doQuery() {
        AccessControl accessControl = new AccessControl();
        if (!accessControl.hasAccess()) {
            return;
        }
        goodsQuery.doQuery();
        RecordQuery recordQuery = new RecordQuery();
        recordQuery.addRecord();
    }
}

@Slf4j
class AccessControl {
    boolean hasAccess() {
        boolean ret = new Random().nextBoolean();
        if (ret) {
            log.info("has access");
        } else {
            log.info("has no access");
        }
        return ret;
    }
}

@Slf4j
class RecordQuery {
    void addRecord() {
        log.info("add a record query");
    }
}
