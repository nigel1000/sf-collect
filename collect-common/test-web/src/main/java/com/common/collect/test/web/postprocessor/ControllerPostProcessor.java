package com.common.collect.test.web.postprocessor;

import com.common.collect.lib.util.ClassUtil;
import com.common.collect.test.web.controller.TestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by hznijianfeng on 2020/3/23.
 */
public class ControllerPostProcessor implements EnvironmentPostProcessor, SmartApplicationListener {
    /**
     * 用于等日志系统初始化后输出内容
     */
    private final static DeferredLog logger = new DeferredLog();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        logger.info("enter into ControllerPostProcessor postProcessEnvironment");
        ClassUtil.changeAnnotationField(TestController.class.getAnnotation(RequestMapping.class), "value", new String[]{"/test"});
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        // 需要等待yaml,properties配置读取完成
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationPreparedEvent) {
            logger.replayTo(ControllerPostProcessor.class);
        }
    }
}
