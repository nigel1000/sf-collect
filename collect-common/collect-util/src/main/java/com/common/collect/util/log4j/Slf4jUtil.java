package com.common.collect.util.log4j;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.FunctionUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2018/11/9.
 */

@Slf4j
public class Slf4jUtil {

    private final static String currentLog4jName = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
    private final static Map<String, Object> loggerMap = new HashMap<>();
    private final static Map<String, String> loggerLevelMap = new HashMap<>();
    private final static String LOG4J12 = "org.slf4j.impl.Log4jLoggerFactory";
    private final static String LOG4J2 = "org.apache.logging.slf4j.Log4jLoggerFactory";
    private final static String SIMPLE = "org.slf4j.impl.SimpleLoggerFactory";
    private final static String LOGBACK = "ch.qos.logback.classic.util.ContextSelectorStaticBinder";
    private final static List<String> levels = Arrays.asList(null, "off", "fatal", "error", "warn", "info", "debug", "trace", "all");

    static {
        initAndSync();
    }

    @SuppressWarnings("unchecked")
    private synchronized static void initAndSync() {
        if (LOG4J12.equals(currentLog4jName)) {
            Log4j12Util.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (SIMPLE.equals(currentLog4jName)) {
            SimpleUtil.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (LOGBACK.equals(currentLog4jName)) {
            LogbackUtil.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (LOG4J2.equals(currentLog4jName)) {
            Log4j2Util.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else {
            throw UnifiedException.gen("Log框架无法识别: type={" + currentLog4jName + "}");
        }
        log.info("当前 Slf4j 使用的 LoggerFactory :[{}],Logger:{}", currentLog4jName, getLoggerNames());
    }

    public static void setLogLevel(String loggerName, String loggerLevel) {
        if (loggerName == null) {
            return;
        }
        if (!levels.contains(loggerLevel)) {
            return;
        }
        Object logger = loggerMap.get(loggerName);
        if (logger == null) {
            initAndSync();
            logger = loggerMap.get(loggerName);
            if (logger == null) {
                return;
            }
        }
        if (currentLog4jName.equals(LOG4J12)) {
            Log4j12Util.setLoggerLevel(logger, loggerLevel);
        } else if (currentLog4jName.equals(SIMPLE)) {
            SimpleUtil.setLoggerLevel(logger, loggerLevel);
        } else if (currentLog4jName.equals(LOGBACK)) {
            LogbackUtil.setLoggerLevel(logger, loggerLevel);
        } else if (currentLog4jName.equals(LOG4J2)) {
            Log4j2Util.setLoggerLevel(logger, loggerLevel);
        }
    }

    public static void setLogLevel(String loggerLevel) {
        if (loggerLevel == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : loggerMap.entrySet()) {
            setLogLevel(entry.getKey(), loggerLevel.toLowerCase());
        }
    }

    public static void recovery(String loggerName) {
        if (loggerName == null) {
            return;
        }
        String loggerLevel = loggerLevelMap.get(loggerName);
        if (loggerLevel != null) {
            loggerLevel = loggerLevel.toLowerCase();
        }
        setLogLevel(loggerName, loggerLevel);
    }

    public static void recovery() {
        for (Map.Entry<String, Object> entry : loggerMap.entrySet()) {
            recovery(entry.getKey());
        }
    }

    public static List<String> getLoggerNames() {
        return FunctionUtil.valueList(loggerMap.keySet(), t -> t);
    }

}

