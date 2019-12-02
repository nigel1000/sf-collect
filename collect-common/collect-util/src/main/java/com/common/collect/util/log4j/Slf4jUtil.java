package com.common.collect.util.log4j;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.util.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hznijianfeng on 2018/11/9.
 */

@Slf4j
public class Slf4jUtil {

    private final static String currentLog4jName = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
    private final static Map<String, Object> loggerMap = new ConcurrentHashMap<>();
    private final static Map<String, String> loggerLevelMap = new ConcurrentHashMap<>();
    private final static String LOG4J12 = "org.slf4j.impl.Log4jLoggerFactory";
    private final static String LOG4J2 = "org.apache.logging.slf4j.Log4jLoggerFactory";
    private final static String SIMPLE = "org.slf4j.impl.SimpleLoggerFactory";
    private final static String LOGBACK = "ch.qos.logback.classic.util.ContextSelectorStaticBinder";
    private final static List<String> levels = Arrays.asList("off", "fatal", "error", "warn", "info", "debug", "trace", "all");

    /**
     * 清空快照数据
     */
    public synchronized static void clear() {
        loggerMap.clear();
        loggerLevelMap.clear();
    }

    /**
     * 刷入当前 Logger 快照
     */
    public synchronized static void refresh() {
        if (LOG4J12.equals(currentLog4jName)) {
            Log4j12Util.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (SIMPLE.equals(currentLog4jName)) {
            SimpleUtil.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (LOGBACK.equals(currentLog4jName)) {
            LogbackUtil.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else if (LOG4J2.equals(currentLog4jName)) {
            Log4j2Util.fillLoggerInfo(loggerMap, loggerLevelMap);
        } else {
            throw UnifiedException.gen("Log 框架无法识别: type={" + getLoggerFactoryClassName() + "}");
        }
    }

    /**
     * like 为空 打印全部
     * like 不为空 包含则打印
     *
     * @param like
     */
    public static void printLoggerInfo(String like) {
        log.info("当前 Slf4j 使用的 LoggerFactory :[{}]", currentLog4jName);
        if (EmptyUtil.isBlank(like)) {
            for (Map.Entry<String, String> entry : loggerLevelMap.entrySet()) {
                log.info("\tLogger :[{}], Level:[{}]", entry.getKey(), entry.getValue());
            }
        } else {
            for (Map.Entry<String, String> entry : loggerLevelMap.entrySet()) {
                if (entry.getKey().contains(like)) {
                    log.info("\tLogger :[{}], Level:[{}]", entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * 获取 LoggerFactory 实例类名
     *
     * @return
     */
    public static String getLoggerFactoryClassName() {
        return currentLog4jName;
    }

    /**
     * like 为空 获取当前所有 Logger 名称
     * like 不为空 获取匹配的 Logger 名称
     *
     * @param like
     * @return
     */
    public static List<String> getLoggerNames(String like) {
        if (EmptyUtil.isBlank(like)) {
            return new ArrayList<>(loggerLevelMap.keySet());
        }
        List<String> names = new ArrayList<>();
        for (String name : loggerLevelMap.keySet()) {
            if (name.contains(like)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * like 为空 修改所有 Log 的 level
     * like 不为空 修改匹配的 Log 的 level
     *
     * @param like
     * @param level
     */
    public static void setLogLevel(String like, String level) {
        if (!levels.contains(level)) {
            throw UnifiedException.gen("不支持设置 level:" + level);
        }
        List<String> logNames = getLoggerNames(like);
        for (String logName : logNames) {
            if (currentLog4jName.equals(LOG4J12)) {
                Log4j12Util.setLoggerLevel(loggerMap.get(logName), level);
            } else if (currentLog4jName.equals(SIMPLE)) {
                SimpleUtil.setLoggerLevel(loggerMap.get(logName), level);
            } else if (currentLog4jName.equals(LOGBACK)) {
                LogbackUtil.setLoggerLevel(loggerMap.get(logName), level);
            } else if (currentLog4jName.equals(LOG4J2)) {
                Log4j2Util.setLoggerLevel(loggerMap.get(logName), level);
            }
        }
    }

}

