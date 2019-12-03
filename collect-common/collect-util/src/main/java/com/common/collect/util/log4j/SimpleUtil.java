package com.common.collect.util.log4j;

import com.common.collect.api.excps.UnifiedException;
import org.slf4j.Logger;
import org.slf4j.impl.SimpleLogger;
import org.slf4j.impl.SimpleLoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.slf4j.spi.LocationAwareLogger;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by nijianfeng on 2018/11/9.
 */
class SimpleUtil {

    public static void fillLoggerInfo(Map<String, Object> loggerMap, Map<String, String> loggerLevelMap) {
        try {
            SimpleLoggerFactory simpleLoggerFactory =
                    (SimpleLoggerFactory) StaticLoggerBinder.getSingleton().getLoggerFactory();
            Field field = SimpleLoggerFactory.class.getDeclaredField("loggerMap");
            field.setAccessible(true);
            ConcurrentMap<String, Logger> simpleLoggerMap = (ConcurrentMap) field.get(simpleLoggerFactory);
            for (Map.Entry<String, Logger> entry : simpleLoggerMap.entrySet()) {
                String loggerName = entry.getKey();
                SimpleLogger logger = (SimpleLogger) entry.getValue();
                loggerMap.put(loggerName, logger);
                Field currentLogLevel = SimpleLogger.class.getDeclaredField("currentLogLevel");
                currentLogLevel.setAccessible(true);
                Integer level = (Integer) currentLogLevel.get(logger);
                if (level == LocationAwareLogger.ERROR_INT) {
                    loggerLevelMap.put(loggerName, "error");
                } else if (level == LocationAwareLogger.WARN_INT) {
                    loggerLevelMap.put(loggerName, "warn");
                } else if (level == LocationAwareLogger.INFO_INT) {
                    loggerLevelMap.put(loggerName, "info");
                } else if (level == LocationAwareLogger.DEBUG_INT) {
                    loggerLevelMap.put(loggerName, "debug");
                } else if (level == LocationAwareLogger.TRACE_INT) {
                    loggerLevelMap.put(loggerName, "trace");
                }
            }
        } catch (Exception ex) {
            throw UnifiedException.gen("解析 SLF4J_SIMPLE SimpleLoggerFactory loggerMap 失败! ", ex);
        }
    }

    public static void setLoggerLevel(Object logger, String loggerLevel) {
        try {
            SimpleLogger simpleLogger = (SimpleLogger) logger;
            if ("error".equalsIgnoreCase(loggerLevel)) {
                loggerLevel = LocationAwareLogger.ERROR_INT + "";
            } else if ("warn".equalsIgnoreCase(loggerLevel)) {
                loggerLevel = LocationAwareLogger.WARN_INT + "";
            } else if ("info".equalsIgnoreCase(loggerLevel)) {
                loggerLevel = LocationAwareLogger.INFO_INT + "";
            } else if ("debug".equalsIgnoreCase(loggerLevel)) {
                loggerLevel = LocationAwareLogger.DEBUG_INT + "";
            } else if ("trace".equalsIgnoreCase(loggerLevel)) {
                loggerLevel = LocationAwareLogger.TRACE_INT + "";
            } else {
                return;
            }
            Field field = SimpleLogger.class.getDeclaredField("currentLogLevel");
            field.setAccessible(true);
            field.set(simpleLogger, Integer.valueOf(loggerLevel));
        } catch (Exception ex) {
            throw UnifiedException.gen("赋值 SimpleLogger currentLogLevel 失败! ", ex);
        }
    }

}
