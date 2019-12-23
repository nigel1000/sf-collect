package com.common.collect.util.log4j;

import com.common.collect.util.NullUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.Collection;
import java.util.Map;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class Log4j2Util {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Collection<Logger> loggers = loggerContext.getLoggers();
        for (Logger logger : loggers) {
            String key = logger.getName();
            if (key == null || "".equals(key.trim())) {
                key = "root";
            }
            loggerMap.put(key, logger);
            loggerLevelMap.put(key,
                    NullUtil.validDefault(() -> logger.getLevel().toString(), null));
        }
    }

    public static void setLoggerLevel(Object logger, String loggerLevel) {
        Logger loggerConfig = (Logger) logger;
        Level targetLevel = Level.toLevel(loggerLevel);
        loggerConfig.setLevel(targetLevel);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(true);
        /* This causes all Loggers to fetch information from their LoggerConfig again. */
        ctx.updateLoggers();
    }

}
