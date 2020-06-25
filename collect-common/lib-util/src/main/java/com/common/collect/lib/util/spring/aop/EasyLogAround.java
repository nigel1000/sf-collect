package com.common.collect.lib.util.spring.aop;

import com.common.collect.lib.util.fastjson.JsonUtil;
import com.common.collect.lib.util.spring.aop.base.DiyAround;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by nijianfeng on 2020/6/25.
 */
@Slf4j
public class EasyLogAround implements DiyAround.IDiyAround {

    private boolean logBefore = true;
    private boolean logAfter = true;
    private boolean logException = true;

    @Override
    public void doBefore(DiyAround.DiyAroundContext diyAroundContext, DiyAround diyAround) {
        if (!logBefore) {
            return;
        }
        log.info("执行之前。模块：{}，类名：{}，方法：{}，入参：{}",
                diyAround.module(),
                diyAroundContext.getClassName(),
                diyAroundContext.getMethodName(),
                JsonUtil.bean2json(diyAroundContext.getArgs()));
    }

    @Override
    public void doAfter(DiyAround.DiyAroundContext diyAroundContext, Object ret, DiyAround diyAround) {
        if (!logAfter) {
            return;
        }
        log.info("执行返回。执行之前。模块：{}，类名：{}，方法：{}，返回：{}",
                diyAround.module(),
                diyAroundContext.getClassName(),
                diyAroundContext.getMethodName(),
                JsonUtil.bean2json(diyAroundContext.getRetValue()));

    }

    @Override
    public void doException(DiyAround.DiyAroundContext diyAroundContext, Throwable throwable, DiyAround diyAround) {
        if (!logException) {
            return;
        }
        log.error("执行异常。模块：{}，类名：{}，方法：{}",
                diyAround.module(),
                diyAroundContext.getClassName(),
                diyAroundContext.getMethodName(),
                diyAroundContext.getException());

    }
}
