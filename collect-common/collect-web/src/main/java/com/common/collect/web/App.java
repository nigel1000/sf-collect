package com.common.collect.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@Configuration
@ComponentScan(basePackages = {"com.common.collect"})
@EnableAutoConfiguration
public class App {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

}
