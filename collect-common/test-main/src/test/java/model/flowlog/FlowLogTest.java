package model.flowlog;

import com.common.collect.model.flowlog.FlowLogManager;
import com.common.collect.model.flowlog.IMetaConfig;
import com.common.collect.model.flowlog.infrastructure.FlowLog;
import com.common.collect.model.flowlog.infrastructure.FlowLogExt;
import com.common.collect.model.flowlog.infrastructure.FlowLogMapperExt;
import com.common.collect.tool.mybatis.MybatisContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nijianfeng on 2020/2/15.
 */

@Slf4j
public class FlowLogTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");
        FlowLogManager flowLogManager = (FlowLogManager) applicationContext.getBean("flowLogManager");

        // 获取执行 sql
        FlowLog flowLog = FlowLogExt.of(FlowLogConfig.DEMO);
        flowLog.setBizId("bizId");
        flowLog.setUpdateValue("null");
        flowLog.setOperatorId("test");
        flowLog.setOperatorName("test");
        log.info("flowLog -> {}", flowLog);
        MybatisContext.setEnableSqlRecord(true);
        flowLogManager.record(FlowLogConfig.DEMO, flowLog);
        log.info("sql -> {}", MybatisContext.getSqlRecord(true));


        FlowLogMapperExt flowLogMapperExt = (FlowLogMapperExt) applicationContext.getBean("flowLogMapperExt");
        log.info("load -> return:{},id:{}", flowLogMapperExt.load(flowLog.getId()), flowLog.getId());
        log.info("delete -> return:{},id:{}", flowLogMapperExt.delete(flowLog.getId()), flowLog.getId());
        log.info("load -> return:{},id:{}", flowLogMapperExt.load(flowLog.getId()), flowLog.getId());

    }

    enum FlowLogConfig implements IMetaConfig {

        DEMO("1", "商品编辑");

        @Getter
        private String bizType;
        @Getter
        private String bizName;

        FlowLogConfig(String bizType, String bizName) {
            this.bizType = bizType;
            this.bizName = bizName;
        }


        @Override
        public String getTableName() {
            return "flow_log";
        }
    }

}
