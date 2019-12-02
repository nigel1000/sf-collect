package com.common.collect.util.log4j;

import com.common.collect.util.NullUtil;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Map;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class Log4j12Util {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        Enumeration enumeration = LogManager.getCurrentLoggers();
        while (enumeration.hasMoreElements()) {
            Logger logger = (Logger) enumeration.nextElement();
            loggerMap.put(logger.getName(), logger);
            loggerLevelMap.put(logger.getName(),
                    NullUtil.validDefault(() -> logger.getLevel().toString(), null));
        }
        Logger rootLogger = LogManager.getRootLogger();
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
