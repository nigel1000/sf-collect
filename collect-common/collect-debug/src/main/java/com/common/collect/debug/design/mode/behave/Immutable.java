package com.common.collect.debug.design.mode.behave;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 不变模式缺少改变自身状态的行为
 */

@Slf4j
public class Immutable {

    public static void main(String[] args) {

        Point point = new Point(2, 5);

        log.info("before:{}", point);
        log.info("move:{}", point.move(-1, 2));
        log.info("before:{}", point);

    }

}

// 强不变模型
// 弱不变模型 去掉final，子类可以修改 x,y
final class Point {

    private int x;
    private int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point move(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

