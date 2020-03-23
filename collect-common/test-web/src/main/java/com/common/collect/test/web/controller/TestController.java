package com.common.collect.test.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hznijianfeng on 2020/3/23.
 */
@RestController
@RequestMapping("")
@Slf4j
public class TestController {

    // http://localhost:8181/test/testControllerPostProcessor
    @RequestMapping(value = "/testControllerPostProcessor", method = {RequestMethod.GET})
    public String testControllerPostProcessor() {
        return "success";
    }

}
