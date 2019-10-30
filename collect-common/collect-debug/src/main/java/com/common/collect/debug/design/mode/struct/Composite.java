package com.common.collect.debug.design.mode.struct;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nijianfeng on 2019/10/30.
 * <p>
 * 合成模式将对象组织到树结构中，可以描述整体和部分的关系。
 * 树的结构
 */

@Slf4j
public class Composite {

    public static void main(String[] args) {
        Component rootDir = new Branch("/");
        rootDir.addComponent(new Leaf("httpclient.jar"));
        rootDir.addComponent(new Branch("bin"));
        rootDir.addComponent(new Leaf("jdbc.md"));
        Component logDir = new Branch("log");
        rootDir.addComponent(logDir);
        logDir.addComponent(new Leaf("app.log"));
        logDir.addComponent(new Leaf("catalina.log"));

        rootDir.print("");

        log.info("###################");

        logDir.print("");

    }

}

@Slf4j
abstract class Component {

    @Getter
    private String content;
    // 是否叶子节点
    @Getter
    private boolean isLeaf;

    Component(String content, boolean isLeaf) {
        this.content = content;
        this.isLeaf = isLeaf;
    }

    // 透明式的合成模式
    void addComponent(Component component) {
        throw new RuntimeException("不支持 addComponent");
    }

    List<Component> getChildren() {
        throw new RuntimeException("不支持 getChildren");
    }

    void print(@NonNull String split) {
        if (this.isLeaf()) {
            log.info(split + this.getContent());
        } else {
            log.info(split + "`" + this.getContent());
            for (Component comp : this.getChildren()) {
                comp.print(split.concat("---"));
            }
        }
    }

}

class Leaf extends Component {

    Leaf(String content) {
        super(content, true);
    }

}

class Branch extends Component {

    private List<Component> children = new ArrayList<>();

    Branch(String content) {
        super(content, false);
    }

    @Override
    void addComponent(Component component) {
        children.add(component);
    }

    @Override
    List<Component> getChildren() {
        return Collections.unmodifiableList(children);
    }

}
