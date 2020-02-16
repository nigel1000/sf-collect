package lib.util;

import com.common.collect.lib.util.NumUtil;

/**
 * Created by hznijianfeng on 2019/3/5.
 */

public class NumUtilTest {

    public static void main(String[] args) {

        System.out.println(NumUtil.parse10kToFen("1"));
        System.out.println(NumUtil.parseFenTo10k("101200"));

    }

}
