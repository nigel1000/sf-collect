package com.common.collect.container.docs;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2019/5/20.
 */

@Data
public class DocsGlobalConfig implements Serializable {

    private boolean reCreate = true;
    private boolean showComment = true;
    private String prefixPath;
    private String pkgPath;

    public void valid() {
        if (EmptyUtil.isEmpty(prefixPath)) {
            throw UnifiedException.gen("路径不能为空");
        }
        if (EmptyUtil.isEmpty(pkgPath)) {
            throw UnifiedException.gen("包路径不能为空");
        }
    }

}
