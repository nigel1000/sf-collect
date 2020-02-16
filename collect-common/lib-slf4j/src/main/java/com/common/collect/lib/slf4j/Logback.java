package com.common.collect.lib.slf4j;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.common.collect.lib.util.NullUtil;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class Logback {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        LoggerContext loggerContext =
                (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : loggerContext.getLoggerList()) {
            loggerMap.put(logger.getName(), logger);
            loggerLevelMap.put(logger.getName(),
                    NullUtil.validDefault(() -> logger.getLevel().toString(), null));
        }
        Logger rootLogger =
                (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        loggerMap.put(rootLogger.getName(), rootLogger);
        loggerLevelMap.put(rootLogger.getName(),
                NullUtil.validDefault(() -> rootLogger.getLevel().toString(), null));
    }

    public static void setLoggerLevel(Object logger, String loggerLevel) {
        Logger targetLogger = (Logger) logger;
        Level targetLevel = Level.toLevel(loggerLevel);
        targetLogger.setLevel(targetLevel);
    }

}
