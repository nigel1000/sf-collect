package com.common.collect.debug.design.mode.behave;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by nijianfeng on 2019/11/3.
 * <p>
 * 方法重载(overload)-静态多分派
 * 方法置换(override)-动态单分派
 * <p>
 * 封装一些施加于某种数据结构元素之上的操作
 */

@Slf4j
public class Visitor {

    public static void main(String[] args) {
        PcComposite pcComposite = new PcComposite();
        pcComposite.addDevice(new Cpu());
        pcComposite.addDevice(new KeyBoard());
        pcComposite.addDevice(new SoftDisk());
        pcComposite.addDevice(new HardDisk());

        log.info("节点 pcComposite 接受 priceVisit 的访问");
        PriceVisit priceVisit = new PriceVisit();
        pcComposite.accept(priceVisit);
        priceVisit.printTotalPrice();

        log.info("节点 pcComposite 接受 totalVisit 的访问");
        TotalVisit totalVisit = new TotalVisit();
        pcComposite.accept(totalVisit);
        totalVisit.printTotalCount();

        pcComposite = new PcComposite();
        pcComposite.addDevice(new Cpu());
        DiskComposite diskComposite = new DiskComposite();
        pcComposite.addDevice(diskComposite);
        diskComposite.addDevice(new SoftDisk());
        diskComposite.addDevice(new HardDisk());

        log.info("节点 pcComposite 接受 priceVisit 的访问");
        priceVisit = new PriceVisit();
        pcComposite.accept(priceVisit);
        priceVisit.printTotalPrice();

        log.info("节点 pcComposite 接受 totalVisit 的访问");
        totalVisit = new TotalVisit();
        pcComposite.accept(totalVisit);
        totalVisit.printTotalCount();

        log.info("节点 diskComposite 接受 priceVisit 的访问");
        priceVisit = new PriceVisit();
        diskComposite.accept(priceVisit);
        priceVisit.printTotalPrice();

        log.info("节点 diskComposite 接受 totalVisit 的访问");
        totalVisit = new TotalVisit();
        diskComposite.accept(totalVisit);
        totalVisit.printTotalCount();

    }

}

interface Visit {

    void visit(Device device);

}

interface Device {

    // 可以做一些运算
    void accept(Visit visit);

    // 提供给访问者的逻辑方法
    BigDecimal price();
}

abstract class CompositeDevice implements Device {
    private java.util.List<Device> deviceList = new ArrayList<>();

    void addDevice(Device device) {
        deviceList.add(device);
    }

    // 叶子节点接受了访问者的访问，树枝节点不被访问
    @Override
    public void accept(Visit visit) {
        for (Device device : deviceList) {
            device.accept(visit);
        }
    }

    @Override
    public BigDecimal price() {
        throw new RuntimeException("不支持限定价格，由 deviceList 动态决定.");
    }
}

class DiskComposite extends CompositeDevice {

}

class PcComposite extends CompositeDevice {

}

class Cpu implements Device {

    @Override
    public void accept(Visit visit) {
        visit.visit(this);
    }

    @Override
    public BigDecimal price() {
        return new BigDecimal("100");
    }
}

class KeyBoard implements Device {

    @Override
    public void accept(Visit visit) {
        visit.visit(this);
    }

    @Override
    public BigDecimal price() {
        return new BigDecimal("400");
    }
}


class SoftDisk implements Device {

    @Override
    public void accept(Visit visit) {
        visit.visit(this);
    }

    @Override
    public BigDecimal price() {
        return new BigDecimal("200");
    }
}

class HardDisk implements Device {

    @Override
    public void accept(Visit visit) {
        visit.visit(this);
    }

    @Override
    public BigDecimal price() {
        return new BigDecimal("300");
    }
}

// 计算叶子节点的价格总和
@Slf4j
class PriceVisit implements Visit {

    private BigDecimal price = BigDecimal.ZERO;

    @Override
    public void visit(Device device) {
        if (!(device instanceof CompositeDevice)) {
            price = device.price().add(price);
        }
    }

    void printTotalPrice() {
        log.info("total price:{}", price);
    }
}

// 计算叶子节点的总数
@Slf4j
class TotalVisit implements Visit {

    private int count = 0;

    @Override
    public void visit(Device device) {
        if (!(device instanceof CompositeDevice)) {
            count++;
        }
    }

    void printTotalCount() {
        log.info("total count:{}", count);
    }
}







