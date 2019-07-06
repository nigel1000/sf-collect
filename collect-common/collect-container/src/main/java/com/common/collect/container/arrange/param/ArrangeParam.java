package com.common.collect.container.arrange.param;

import lombok.Data;

import java.util.List;

@Data
public class ArrangeParam {

    private String type;
    private String name;
    private List<String> input;

}