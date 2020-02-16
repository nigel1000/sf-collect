package com.common.collect.test.main.mode.behave;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/11/3.
 * <p>
 * 管理很多对象的相互作用，以便使这些对象专注于自身的行为。
 */
public class Mediator {

    public static void main(String[] args) {
        WTO wto = new WTO();
        China china = new China(wto);
        America america = new America(wto);
        Japan japan = new Japan(wto);

        wto.setChina(china);
        wto.setAmerica(america);
        wto.setJapan(japan);

        china.sayTransactionViaWTO("五星红旗迎风飘扬");
        america.sayTransactionViaWTO("自由 民主 平等");

    }

}

// 协调者
class WTO {

    @Setter
    private China china;
    @Setter
    private America america;
    @Setter
    private Japan japan;

    void sendChinaTransaction(String content) {
        america.receiveChinaTransaction(content);
        japan.receiveChinaTransaction(content);
    }

    void sendAmericaTransaction(String content) {
        china.receiveAmericaTransaction(content);
        japan.receiveAmericaTransaction(content);
    }

}

// 被协调者
abstract class Country {

    WTO wto;

    Country(WTO wto) {
        this.wto = wto;
    }

    void sayTransactionViaWTO(String content) {
    }
}

@Slf4j
class China extends Country {
    China(WTO wto) {
        super(wto);
    }

    @Override
    void sayTransactionViaWTO(String content) {
        wto.sendChinaTransaction(content);
    }

    void receiveAmericaTransaction(String content) {
        log.info("china receiveAmericaTransaction:{}", content);
    }
}

@Slf4j
class America extends Country {
    America(WTO wto) {
        super(wto);
    }

    @Override
    void sayTransactionViaWTO(String content) {
        wto.sendAmericaTransaction(content);
    }

    void receiveChinaTransaction(String content) {
        log.info("america receiveChinaTransaction:{}", content);
    }
}

@Slf4j
class Japan extends Country {
    Japan(WTO wto) {
        super(wto);
    }

    void receiveChinaTransaction(String content) {
        log.info("japan receiveChinaTransaction:{}", content);
    }

    void receiveAmericaTransaction(String content) {
        log.info("japan receiveAmericaTransaction:{}", content);
    }
}