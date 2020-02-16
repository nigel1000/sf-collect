package com.common.collect.test.web;

import com.common.collect.framework.redis.RedisClient;
import com.common.collect.lib.util.framework.trace.filter.web.TraceIdFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by nijianfeng on 2020/1/11.
 */
@PropertySource("classpath:db.properties")
@Configuration
@ComponentScan(basePackages = {
        "com.common.collect.test.web",
//        "com.common.collect.model.taskrecord",
//        "com.common.collect.model.flowlog",
        "com.common.collect.util.spring"
})
@EnableAutoConfiguration
public class App {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    @Bean
    public RedisClient redisClient() {
        return new RedisClient();
    }

}
