package com.common.collect.test.main.mode.struct;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2019/10/31.
 * <p>
 * 外部与一个子系统的通信必须通过一个统一的门面对象进行
 */
public class Facade {

    public static void main(String[] args) {
        TradeFacade tradeFacade = new TradeFacade();
        tradeFacade.order();
    }

}

@Slf4j
class TradeFacade {
    private GoodsService goodsService = new GoodsService();
    private CartService cartService = new CartService();
    private PayService payService = new PayService();
    private CouponService couponService = new CouponService();

    void order() {
        goodsService.queryGoods();
        cartService.addGoods2Cart();
        couponService.useCoupon();
        payService.payOrder();
    }
}

@Slf4j
class GoodsService {
    void queryGoods() {
        log.info("queryGoods");
    }
}

@Slf4j
class CartService {
    void addGoods2Cart() {
        log.info("addGoods2Cart");
    }

    void subGoods2Cart() {
        log.info("subGoods2Cart");
    }
}

@Slf4j
class PayService {
    void payOrder() {
        log.info("payOrder");
    }
}

@Slf4j
class CouponService {
    void useCoupon() {
        log.info("useCoupon");
    }
}