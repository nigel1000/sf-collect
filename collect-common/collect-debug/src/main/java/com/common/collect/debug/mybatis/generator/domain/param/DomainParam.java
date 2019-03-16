package com.common.collect.debug.mybatis.generator.domain.param;

import com.common.collect.api.excps.UnifiedException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/14.
 */

@Data
public class DomainParam {

    private GlobalParam globalParam;

    private String prefixPath;
    private List<String> tableNames;

    // 包路径
    // package ${domainPackage};
    private String packagePath;

    public DomainParam(GlobalParam globalParam) {
        this.prefixPath = globalParam.getPrefixPath();
        this.tableNames = globalParam.getTableNames();
        this.globalParam = globalParam;
    }

    public void validSelf() {
        if (StringUtils.isBlank(prefixPath)) {
            throw UnifiedException.gen("Domain 文件导出路径不能为空");
        }

        if (StringUtils.isBlank(packagePath)) {
            throw UnifiedException.gen("Domain 包路径配置不能为空");
        }
    }

}
