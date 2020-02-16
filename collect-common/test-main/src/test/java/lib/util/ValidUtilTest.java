package lib.util;

import com.common.collect.lib.util.ValidUtil;

/**
 * Created by nijianfeng on 2020/2/16.
 */
public class ValidUtilTest {

    public static void main(String[] args) {
        System.out.println(ValidUtil.ge("12",3));
        System.out.println(ValidUtil.ge("3",3));
        System.out.println(ValidUtil.ge("2",3));
    }

}
