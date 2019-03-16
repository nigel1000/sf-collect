package com.common.collect.util.log4j;

import com.common.collect.util.NullUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Map;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class Log4j2Util {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Map<String, LoggerConfig> map = loggerContext.getConfiguration().getLoggers();
        for (LoggerConfig loggerConfig : map.values()) {
            String key = loggerConfig.getName();
            if (key == null || "".equals(key.trim())) {
                key = "root";
            }
            loggerMap.putIfAbsent(key, loggerConfig);
            loggerLevelMap.putIfAbsent(key,
                    NullUtil.validDefault(() -> loggerConfig.getLevel().toString(), null));
        }
    }

    public static void setLoggerLevel(Object logger, String loggerLevel) {
        LoggerConfig loggerConfig = (LoggerConfig) logger;
        Level targetLevel = Level.toLevel(loggerLevel);
        loggerConfig.setLevel(targetLevel);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        /* This causes all Loggers to fetch information from their LoggerConfig again. */
        ctx.updateLoggers();
    }

}
