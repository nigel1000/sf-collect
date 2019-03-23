package collect.debug.aop.order;

import collect.debug.aop.IAopOrderAspect1;
import collect.debug.aop.IAopOrderAspect2;
import collect.debug.aop.IAopOrderAspect3;
import collect.debug.mybatis.TestMybatis;
import collect.debug.mybatis.dao.TestMapper;
import collect.debug.mybatis.domain.Test;
import com.common.collect.api.excps.UnifiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by hznijianfeng on 2019/2/22.
 */

@Slf4j
@Component
public class AopOrder {

    @Resource
    private TestMapper testMapper;

    @IAopOrderAspect1
    @IAopOrderAspect2
    public void normal() {
        log.info("{} in action start", this.getClass().getName());
        log.info("{} in action end", this.getClass().getName());
    }

    @IAopOrderAspect1
    @IAopOrderAspect2
    @IAopOrderAspect3
    public void exception() {
        log.info("{} in action start", this.getClass().getName());
        log.info("{} in action throw exception", this.getClass().getName());
        throw UnifiedException.gen("抛出异常");
    }

    @IAopOrderAspect3(rollback = true)
    @Transactional
    public void rollback() {
        log.info("{} in action start", this.getClass().getName());
        Test test = TestMybatis.genTest();
        log.info("create -> return:{},id:{}", testMapper.create(test), test.getId());
        log.info("{} in action end", this.getClass().getName());
    }

}
