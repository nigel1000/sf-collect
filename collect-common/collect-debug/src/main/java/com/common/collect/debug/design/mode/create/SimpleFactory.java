package com.common.collect.debug.design.mode.create;

import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/25.、
 *
 * 具体工厂 ShapeCreator
 * 产品抽象 Shape
 */

@Slf4j
public class SimpleFactory {

    public static void main(String[] args) {
        Shape shape = ShapeCreator.create(ShapeCreator.square);
        shape.draw();
        shape.erase();

        shape = ShapeCreator.create(ShapeCreator.circle);
        shape.draw();
        shape.erase();

        try {
            shape = ShapeCreator.create("triangle 三角形");
        } catch (Exception ex) {
            log.info("exception:", ex);
        }
    }

}

class ShapeCreator {

    static final String square = Square.class.getSimpleName();
    static final String circle = Circle.class.getSimpleName();

    public static Shape create(String shape) {
        if (square.equals(shape)) {
            return new Square();
        } else if (circle.equals(shape)) {
            return new Circle();
        } else {
            throw new RuntimeException(StringUtil.format("无法创建 {}", shape));
        }
    }
}

interface Shape {
    // 画
    void draw();

    // 擦
    void erase();
}

// 正方形
@Slf4j
class Square implements Shape {

    @Override
    public void draw() {
        log.info("{} draw", this.getClass().getSimpleName());
    }

    @Override
    public void erase() {
        log.info("{} erase", this.getClass().getSimpleName());
    }
}

// 圆形
@Slf4j
class Circle implements Shape {

    @Override
    public void draw() {
        log.info("{} draw", this.getClass().getSimpleName());
    }

    @Override
    public void erase() {
        log.info("{} erase", this.getClass().getSimpleName());
    }

}


