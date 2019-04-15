package com.common.collect.util;


import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by hznijianfeng on 2018/8/15.
 */

public class NumUtil {

    // 万转分
    public static String parse10kToFen(String money) {
        if (EmptyUtil.isBlank(money)) {
            throw UnifiedException.gen("转换金额不能为空");
        }
        return stripTrailingZeros(new BigDecimal(money).multiply(new BigDecimal(1000000)));
    }

    // 分转万
    public static String parseFenTo10k(String money) {
        if (EmptyUtil.isBlank(money)) {
            throw UnifiedException.gen("转换金额不能为空");
        }
        return stripTrailingZeros(divide(new BigDecimal(money), new BigDecimal(1000000), 6));
    }

    // a/b
    // scale 精度
    public static BigDecimal divide(@NonNull BigDecimal dividend, @NonNull BigDecimal divisor, int scale) {
        return dividend.divide(divisor, scale, RoundingMode.DOWN);
    }

    public static String stripTrailingZeros(BigDecimal decimal) {
        return decimal.stripTrailingZeros().toPlainString();
    }

}
