package com.common.collect.test.debug.spring;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.test.debug.arrange.context.ConfigContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ContextRefreshedApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        log.info("加载 arrange 配置开始 ....... ");
        try {
            ConfigContext.load(new ClassPathResource("arrange/function-define.yml").getInputStream());
        }catch (IOException ex){
            throw UnifiedException.gen("加载 arrange 配置异常 ....... ",ex);
        }

        log.info("加载 arrange 配置结束 ....... ");

    }
}