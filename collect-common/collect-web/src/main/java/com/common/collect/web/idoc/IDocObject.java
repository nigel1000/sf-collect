package com.common.collect.web.idoc;

import com.common.collect.container.idoc.IDocField;
import lombok.Data;

import java.util.List;

@Data
public class IDocObject {
    @IDocField(nameDesc = "名称", desc = "小于十个字符")
    private String name;
    private String key;
    @IDocField
    private IDocObjectSub iDocObjectSub;
    private List<IDocObjectSub> iDocObjectSubs;
    @IDocField
    private List<Long> longs;

}