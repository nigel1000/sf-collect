package com.common.collect.container.elastic;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by hznijianfeng on 2019/1/30.
 */

@Data
public class JoinField implements Serializable {

    // the name of the join for this document
    private String name;

    // the parent id of this child document
    private String parent;

    public JoinField() {
    }

    public JoinField(String name) {
        this.name = name;
    }

    public JoinField(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }
}
