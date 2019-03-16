package com.common.collect.util.log4j;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.common.collect.util.NullUtil;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class LogbackUtil {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        LoggerContext loggerContext =
                (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : loggerContext.getLoggerList()) {
            loggerMap.putIfAbsent(logger.getName(), logger);
            loggerLevelMap.putIfAbsent(logger.getName(),
                    NullUtil.validDefault(() -> logger.getLevel().toString(), null));
        }
        Logger rootLogger =
                (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        loggerMap.putIfAbsent(rootLogger.getName(), rootLogger);
        loggerLevelMap.putIfAbsent(rootLogger.getName(),
                NullUtil.validDefault(() -> rootLogger.getLevel().toString(), null));
    }

    public static void setLoggerLevel(Object logger, String loggerLevel) {
        Logger targetLogger = (Logger) logger;
        Level targetLevel = Level.toLevel(loggerLevel);
        targetLogger.setLevel(targetLevel);
    }

}
