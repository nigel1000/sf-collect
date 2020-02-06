package com.common.collect.debug.design.mode.struct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created by nijianfeng on 2019/10/30.
 * <p>
 * 以共享的方式高效的支持大量细粒度对象
 * 可以共享的状态为内蕴状态，属性化
 * 不可以共享的状态为外蕴状态，参数化
 * <p>
 * 不变模式是内部状态不允许变化
 * 享元模式是内蕴状态变化不影响共享
 */
public class Flyweight {

    public static void main(String[] args) {
        List<Cafe> cafeList = new ArrayList<>();
        int num = 1;
        cafeList.add(CafeShop.getCafe("Capucino"));
        cafeList.add(CafeShop.getCafe("BlackCoffee"));
        cafeList.add(CafeShop.getCafe(Arrays.asList("BlackCoffee", "Capucino")));
        cafeList.add(CafeShop.getCafe("Espresso"));
        cafeList.add(CafeShop.getCafe(Arrays.asList("Espresso", "BlackCoffee", "BlackCoffee")));
        cafeList.add(CafeShop.getCafe("BlackCoffee"));

        for (Cafe cafe : cafeList) {
            cafe.offerCafe(new Table(num++));
        }

        CafeShop.showCafeKind();

    }

}

abstract class Cafe {

    String kind;

    Cafe(String kind) {
        this.kind = kind;
    }

    abstract void offerCafe(Table table);

}

@Slf4j
// 套餐 一杯一杯点
// 单纯享元模式
class CafeKind extends Cafe {

    CafeKind(String kind) {
        super(kind);
    }

    @Override
    public void offerCafe(Table table) {
        log.info("table num:{}, cafe kind:{}",
                table.getTableNum(), this.kind);
    }
}

@Slf4j
// 套餐 一桌多杯
// 复合享元模式
class CafeKindComposite extends Cafe {

    private List<Cafe> cafeList = new ArrayList<>();

    CafeKindComposite() {
        super(null);
    }

    void addCafe(Cafe cafe) {
        cafeList.add(cafe);
    }

    @Override
    public void offerCafe(Table table) {
        log.info("start offer composite cafe ");
        for (Cafe cafe : cafeList) {
            log.info("table num:{}, cafe kind:{}",
                    table.getTableNum(), cafe.kind);
        }
        log.info("end offer composite cafe ");
    }
}

class Table {

    @Getter
    private int tableNum;

    public Table(int tableNum) {
        this.tableNum = tableNum;
    }

}

@Slf4j
class CafeShop {

    private static Map<String, Cafe> cafeList = new HashMap<>();

    static Cafe getCafe(String kind) {
        Cafe ret = cafeList.get(kind);
        if (ret == null) {
            ret = new CafeKind(kind);
            cafeList.putIfAbsent(kind, ret);
        }
        return ret;
    }

    static Cafe getCafe(List<String> kinds) {
        CafeKindComposite ret = new CafeKindComposite();
        for (String kind : kinds) {
            ret.addCafe(new CafeKind(kind));
        }
        return ret;
    }

    static void showCafeKind() {
        log.info("Total cafe kind make:{}", cafeList.keySet());
    }

}
