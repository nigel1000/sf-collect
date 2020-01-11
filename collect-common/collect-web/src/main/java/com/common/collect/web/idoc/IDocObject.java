package com.common.collect.web.idoc;

import com.common.collect.container.idoc.IDocField;
import lombok.Data;

import java.util.List;

@Data
public class IDocObject {
    @IDocField(nameDesc = "名称", desc = "小于十个字符")
    private String name;
    private String key;
    @IDocField(value = "{name:11)")
    private IDocObjectSub iDocObjectSub;
    private List<IDocObjectSub> iDocObjectSubs;
    @IDocField(value = "[1,2,4]")
    private List<Long> longs;

}