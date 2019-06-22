package collect.debug;

import com.common.collect.container.TransactionHelper;
import com.common.collect.container.mybatis.MybatisContext;
import com.common.collect.model.flowlog.FlowLog;
import com.common.collect.model.flowlog.FlowLogService;
import com.common.collect.model.flowlog.IMetaConfig;
import com.common.collect.model.flowlog.mapper.FlowLogMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class FlowLogTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");

        FlowLogService flowLogService = (FlowLogService) applicationContext.getBean("flowLogService");
        FlowLogMapper flowLogMapper = (FlowLogMapper) applicationContext.getBean("flowLogMapper");

        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");
        transactionHelper.aroundBiz(() -> {
            // 获取执行 sql
            FlowLog flowLog = FlowLog.gen("bizId", "null", "test", "test");
            log.info("flowLog -> {}", flowLog);

            MybatisContext.setEnableSqlRecord(true);
            flowLogService.record(flowLog, FlowLogConfig.DEMO);
            log.info("sql -> {}", MybatisContext.getSqlRecord(true));
            log.info("load -> return:{},id:{}", flowLogMapper.load(flowLog.getId()), flowLog.getId());
            log.info("delete -> return:{},id:{}", flowLogMapper.delete(flowLog.getId()), flowLog.getId());

            flowLog.setId(null);

            MybatisContext.setEnableSqlRecord(true);
            flowLogService.create(flowLog, FlowLogConfig.DEMO);
            log.info("sql -> {}", MybatisContext.getSqlRecord(true));
            log.info("load -> return:{},id:{}", flowLogMapper.load(flowLog.getId()), flowLog.getId());
            log.info("delete -> return:{},id:{}", flowLogMapper.delete(flowLog.getId()), flowLog.getId());
        });
    }

    enum FlowLogConfig implements IMetaConfig {

        DEMO("运营后台", "商品编辑");

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
