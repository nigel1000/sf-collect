package com.common.collect.util;


import com.common.collect.api.excps.UnifiedException;

import java.math.BigDecimal;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

public class NumUtil {

    // 万转分
    public static String parse10kToFen(String money) {
        if (money == null || "".equals(money.trim())) {
            throw UnifiedException.gen("转换金额不能为空");
        }
        BigDecimal bigDecimal = new BigDecimal(money);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(1000000));
        return multiply.stripTrailingZeros().toPlainString();
    }

    // 分转万
    public static String parseFenTo10k(String money) {
        if (money == null || "".equals(money.trim())) {
            throw UnifiedException.gen("转换金额不能为空");
        }
        BigDecimal bigDecimal = new BigDecimal(money);
        BigDecimal divide = bigDecimal.divide(new BigDecimal(1000000), 6, BigDecimal.ROUND_DOWN);
        return divide.stripTrailingZeros().toPlainString();
    }

}
