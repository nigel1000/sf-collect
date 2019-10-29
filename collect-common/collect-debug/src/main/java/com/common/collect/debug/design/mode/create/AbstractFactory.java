package com.common.collect.debug.design.mode.create;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/28.
 * 向客户端提供一个接口，创建一个产品中的产品结构对象。
 *
 * 产品：window mac linux
 * 产品结构：ram cpu
 *
 * 一个 window 的产品簇：windowRam windowCpu
 */
public class AbstractFactory {

    public static void main(String[] args) {
        // 生产 window 的产品簇
        ComputerCreator creator = new WindowComputerCreator();
        creator.createCpu();
        creator.createRam();

        // 生产 mac 的生产簇
        creator = new MacComputerCreator();
        creator.createCpu();
        creator.createRam();

    }

}

interface ComputerCreator {

    default Ram createRam() {
        return null;
    }

    default Cpu createCpu() {
        return null;
    }
}

class WindowComputerCreator implements ComputerCreator {
    @Override
    public Cpu createCpu() {
        return new WindowCpu();
    }

    @Override
    public Ram createRam() {
        return new WindowRam();
    }
}

class MacComputerCreator implements ComputerCreator {
    @Override
    public Cpu createCpu() {
        return new MacCpu();
    }

    @Override
    public Ram createRam() {
        return new MacRam();
    }
}


interface Ram {
}

@Slf4j
class WindowRam implements Ram {
    WindowRam() {
        log.info("WindowRam created");
    }
}

@Slf4j
class MacRam implements Ram {
    MacRam() {
        log.info("MacRam created");
    }
}

interface Cpu {
}

@Slf4j
class WindowCpu implements Cpu {
    WindowCpu() {
        log.info("WindowCpu created");
    }
}

@Slf4j
class MacCpu implements Cpu {
    MacCpu() {
        log.info("MacCpu created");
    }
}