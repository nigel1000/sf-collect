package com.common.collect.debug.design.mode.create;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by nijianfeng on 2019/10/29.
 * <p>
 * 复制原型对象创建出同类型的对象
 */

@Slf4j
public class Prototype {

    public static void main(String[] args) throws Exception {

        AppleTree appleTree = new AppleTree();

        log.info("appleTree:{}", appleTree);
        log.info("appleTree.clone():{}", appleTree.clone());
        log.info("appleTree.deepClone():{}", appleTree.deepClone());
    }
}

class AppleTree implements Cloneable, Serializable {

    // socket stream 循环依赖 等无法序列化的字段需要排除
    private transient String name = "富士山苹果树";

    private Integer age = 3;

    private Integer height = 20;

    private Apple apple = new Apple();

    Object deepClone() throws Exception {
        // 对象写到流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);

        // 流转对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "AppleTree{" +
                "apple=" + apple.hashCode() +
                ", apple=" + apple.toString() +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                '}';
    }
}

class Apple implements Cloneable, Serializable {

    // socket stream 循环依赖 等无法序列化的字段需要排除
    private transient String name = "红苹果";

    private String color = "red";

    private Boolean isSweet = true;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Apple{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", isSweet=" + isSweet +
                '}';
    }
}

