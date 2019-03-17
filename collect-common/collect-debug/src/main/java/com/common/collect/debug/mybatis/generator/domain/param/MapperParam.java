package com.common.collect.debug.mybatis.generator.domain.param;

import com.common.collect.api.excps.UnifiedException;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Data
public class MapperParam {

    private GlobalParam globalParam;

    // 文件生成位置
    private String daoPrefixPath;
    // 文件生成位置
    private String mapperPrefixPath;
    private List<String> tableNames;

    // java 文件
    private boolean genDao = true;
    // xml 文件
    private boolean genMapper = true;

    // 包路径
    private String daoPackagePath;
    private String domainPackagePath;
    // dao class 后缀 默认Mapper
    private String daoSuffixName = "mapper";

    private List<String> sqlIds = Lists.newArrayList("create", "creates", "update", "load", "loads", "delete", "deletes");
    private List<String> insertDate2Now = Lists.newArrayList("createdAt", "updatedAt", "createAt", "updateAt");

    public MapperParam(GlobalParam globalParam) {
        this.daoPrefixPath = globalParam.getPrefixPath();
        this.mapperPrefixPath = globalParam.getPrefixPath();
        this.tableNames = globalParam.getTableNames();
        this.globalParam = globalParam;
    }

    public void validSelf() {
        if (genDao && StringUtils.isBlank(daoPrefixPath)) {
            throw UnifiedException.gen("daoPrefixPath 文件位置 不能为空");
        }

        if (genMapper && StringUtils.isBlank(mapperPrefixPath)) {
            throw UnifiedException.gen("mapperPrefixPath 文件位置 不能为空");
        }

        if (StringUtils.isBlank(daoPackagePath)) {
            throw UnifiedException.gen("daoPackagePath 不能为空");
        }

        if (StringUtils.isBlank(domainPackagePath)) {
            throw UnifiedException.gen("domainPackagePath 不能为空");
        }

    }

}
