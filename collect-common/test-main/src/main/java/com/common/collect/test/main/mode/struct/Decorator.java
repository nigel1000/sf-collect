package com.common.collect.test.main.mode.struct;

import com.common.collect.lib.util.DateUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/10/30.
 * <p>
 * 以对客户端透明对方式扩展对象对功能
 * 改变功能不改变接口
 */
public class Decorator {

    public static void main(String[] args) {
        Print print = new FacebookHeaderPrintDecorator(
                new CommonFooterPrintDecorator(
                        new OrderPrint()));
        print.addOrderItem(new OrderItem("轮胎", 3, new BigDecimal("201.22")));
        print.addOrderItem(new OrderItem("牛仔裤", 8, new BigDecimal("80.79")));
        print.print();
    }

}

// 构件
interface Print {

    void print();

    void addOrderItem(OrderItem orderItem);

    // 为 footer 扩展的一个接口
    BigDecimal getTotalPrice();

}

@Getter
class OrderItem {

    private String name;
    private Integer count;
    private BigDecimal price;

    OrderItem(String name, Integer count, BigDecimal price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }
}

// 具体构件
// 核心功能 打印订单详细
// 先有打印订单的功能 后期要扩展打印不同的头部和尾部
@Slf4j
class OrderPrint implements Print {

    private List<OrderItem> orderItemList = new ArrayList<>();

    @Override
    public void print() {
        log.info("========================================");
        log.info("name\tcount\tprice\t");
        for (OrderItem orderItem : orderItemList) {
            log.info("{}\t{}\t{}\t", orderItem.getName(), orderItem.getCount(), orderItem.getPrice());
        }
        log.info("========================================");
    }

    @Override
    public BigDecimal getTotalPrice() {
        BigDecimal ret = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            ret = ret.add(orderItem.getPrice());
        }
        return ret;
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
    }

}

// 抽象装饰类
abstract class PrintDecorator implements Print {

    private Print print;

    PrintDecorator(Print print) {
        this.print = print;
    }

    @Override
    public void print() {
        print.print();
    }

    @Override
    public BigDecimal getTotalPrice() {
        return print.getTotalPrice();
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        print.addOrderItem(orderItem);
    }
}

// 具体装饰类
@Slf4j
class FacebookHeaderPrintDecorator extends PrintDecorator {

    FacebookHeaderPrintDecorator(Print print) {
        super(print);
    }

    @Override
    public void print() {
        //功能增强
        printHeader();
        super.print();
    }

    private void printHeader() {
        log.info("company: facebook");
        log.info("date: {}", DateUtil.now());
    }
}

@Slf4j
class CommonFooterPrintDecorator extends PrintDecorator {

    CommonFooterPrintDecorator(Print print) {
        super(print);
    }

    @Override
    public void print() {
        super.print();
        //功能增强
        printFooter();
    }

    private void printFooter() {
        log.info("total: {}", getTotalPrice());
    }
}