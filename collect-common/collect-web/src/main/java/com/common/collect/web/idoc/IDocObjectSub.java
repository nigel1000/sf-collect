package com.common.collect.web.idoc;

import com.common.collect.container.idoc.IDocField;
import lombok.Data;

@Data
public class IDocObjectSub {
    @IDocField(nameDesc = "名称", desc = "小于十个字符")
    private String nameSub;
    private String keySub;
}