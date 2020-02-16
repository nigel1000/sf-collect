package com.common.collect.lib.util;

import com.common.collect.lib.api.excps.UnifiedException;

import java.io.File;

/**
 * Created by nijianfeng on 2020/2/15.
 */
public class SystemUtil {

    public static String getTempDir() {
        String dir = System.getProperty("java.io.tmpdir");
        if (EmptyUtil.isEmpty(dir)) {
            throw UnifiedException.gen("不存在临时目录");
        }
        return dir + pathSeparator();
    }

    public static String pathSeparator() {
        return File.pathSeparator;
    }

}
