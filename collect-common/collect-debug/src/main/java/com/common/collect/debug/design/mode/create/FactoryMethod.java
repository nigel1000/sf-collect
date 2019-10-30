package com.common.collect.debug.design.mode.create;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/28.
 * <p>
 * 定义一个创建产品对象的工厂接口，将实际创建工作推迟到子类中
 * 工厂抽象 ConnectionCreator
 * 产品抽象 Connection
 */
public class FactoryMethod {

    public static void main(String[] args) {
        ConnectionCreator creator = new MysqlConnectionCreator();
        Connection conn = creator.createConnection();
        conn.ping();

        creator = new SybaseConnectionCreator();
        conn = creator.createConnection();
        conn.ping();
    }

}

interface Connection {
    default void ping() {
    }
}

interface ConnectionCreator {

    default Connection createConnection() {
        return null;
    }
}

@Slf4j
class MysqlConnectionCreator implements ConnectionCreator {
    @Override
    public Connection createConnection() {
        return new MysqlConnection();
    }
}

@Slf4j
class SybaseConnectionCreator implements ConnectionCreator {
    @Override
    public Connection createConnection() {
        return new SybaseConnection();
    }
}

@Slf4j
class MysqlConnection implements Connection {

    MysqlConnection() {
        log.info("MysqlConnection created");
    }

    @Override
    public void ping() {
        log.info("MysqlConnection ping");
    }
}

@Slf4j
class SybaseConnection implements Connection {

    SybaseConnection() {
        log.info("SybaseConnection created");
    }

    @Override
    public void ping() {
        log.info("SybaseConnection ping");
    }
}