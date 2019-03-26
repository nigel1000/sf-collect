package com.common.collect.debug.mybatis.generator.domain.param;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.DateUtil;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Data
public class GlobalParam {

    // db 配置
    private String dbSchema;
    private String dbUrl;
    private String dbUser;
    private String dbPwd;

    /**
     * Created by ${author} on ${date}.
     */
    private String author = "system";
    private String date = DateUtil.format(DateUtil.now(), "yyyy/MM/dd");

    // 生成文件地址
    private String prefixPath;
    // 可为空 空代表导出 schema 下全部
    private List<String> tableNames;

    public void validSelf() {
        if (EmptyUtil.isBlank(dbSchema)
                || EmptyUtil.isBlank(dbUrl)
                || EmptyUtil.isBlank(dbUser)
                || EmptyUtil.isBlank(dbPwd)) {
            throw UnifiedException.gen("db 配置不能为空");
        }
    }

}
