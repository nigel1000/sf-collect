package com.common.collect.debug.design.mode.behave;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/11/1.
 * <p>
 * 存储另外一个对象内部状态的快照
 */
public class Memento {

    public static void main(String[] args) {
        GameRoleSavePointManager manager = new GameRoleSavePointManager();
        GameRole gameRole = new GameRole("八哥");
        gameRole.showCurrentState();
        manager.addMemento(1, gameRole.createMemento());

        gameRole.fail();
        gameRole.showCurrentState();
        manager.addMemento(2, gameRole.createMemento());

        gameRole.win();
        gameRole.showCurrentState();
        manager.addMemento(3, gameRole.createMemento());

        gameRole.recoverMemento(manager.getMemento(3));
        gameRole.showCurrentState();

        gameRole.recoverMemento(manager.getMemento(2));
        gameRole.showCurrentState();

        gameRole.recoverMemento(manager.getMemento(1));
        gameRole.showCurrentState();
    }

}

// 窄接口
// 不提供写，可提供保存状态的读取即get
// 不提供写也不提供读
interface NarrowMemento {
}

// 备忘录管理员
class GameRoleSavePointManager {

    // 管理备忘记录
    private Map<Integer, NarrowMemento> savePoint = new HashMap<>();

    void addMemento(Integer pointName, NarrowMemento memento) {
        savePoint.put(pointName, memento);
    }

    NarrowMemento getMemento(Integer pointName) {
        return savePoint.get(pointName);
    }

}

// 发起者
@Slf4j
class GameRole {

    // 生命值
    private int lifeNum = 50;
    // 战绩
    private int winCnt = 0;
    // 角色名称
    private String name;

    GameRole(String name) {
        this.name = name;
    }

    void win() {
        winCnt++;
        lifeNum += 10;
    }

    void fail() {
        winCnt--;
        lifeNum -= 10;
    }

    void showCurrentState() {
        log.info("name:{},lifeNum:{},winCnt:{}", name, lifeNum, winCnt);
    }

    // 创建备忘
    NarrowMemento createMemento() {
        WideMemento wideMemento = new WideMemento();
        wideMemento.setLifeNum(lifeNum);
        wideMemento.setWinCnt(winCnt);
        return wideMemento;
    }

    // 从备忘恢复
    void recoverMemento(NarrowMemento memento) {
        WideMemento wideMemento = (WideMemento) memento;
        this.lifeNum = wideMemento.getLifeNum();
        this.winCnt = wideMemento.getWinCnt();
    }

    // 宽接口
    class WideMemento implements NarrowMemento {

        // 生命值
        int lifeNum;
        // 战绩
        int winCnt;

        int getLifeNum() {
            return lifeNum;
        }

        void setLifeNum(int lifeNum) {
            this.lifeNum = lifeNum;
        }

        int getWinCnt() {
            return winCnt;
        }

        void setWinCnt(int winCnt) {
            this.winCnt = winCnt;
        }
    }
}




