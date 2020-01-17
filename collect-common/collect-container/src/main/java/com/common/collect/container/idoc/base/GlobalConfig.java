package com.common.collect.container.idoc.base;

import java.util.Objects;

/**
 * Created by nijianfeng on 2020/1/11.
 */
public class GlobalConfig {

    // 是否重新创建
    public static Boolean reCreate;

    public static String directReturnKey = "asdfghjklqwertyuiop";
    public static String directReturnKeyShow = "result";

    public static String switchDirectReturnKey(String directReturnKey) {
        if (Objects.equals(directReturnKey, GlobalConfig.directReturnKey)) {
            return directReturnKeyShow;
        }
        return directReturnKey;
    }

}
