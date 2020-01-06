package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.TransactionHelper;
import com.common.collect.model.retry.AbstractRetryProcess;
import com.common.collect.model.retry.IMetaConfig;
import com.common.collect.model.retry.RetryRecord;
import com.common.collect.model.retry.RetryRecordManager;
import com.common.collect.util.EmptyUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class RetryRecordTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");

        RetryRecordManager retryRecordManager = (RetryRecordManager) applicationContext.getBean("retryRecordManager");

        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");
        transactionHelper.aroundBiz(() -> {
            retryRecordManager.record(RetryRecordConfig.DEMO, "1", "body", 3, UnifiedException.gen("exception"));
            List<RetryRecord> retryRecordList = retryRecordManager.loadNeedRetryRecord(RetryRecordConfig.DEMO, 0);
            log.info("loadNeedRetryRecord -> return:{}", retryRecordList);
            AbstractRetryProcess retryProcess = new AbstractRetryProcess() {
                @Override
                public IMetaConfig metaConfig() {
                    return RetryRecordConfig.DEMO;
                }

                @Override
                public boolean bizExecute(RetryRecord retryRecord) throws Exception {
                    return false;
                }
            };
            int count = 1;
            while (EmptyUtil.isNotEmpty(retryRecordList)) {
                retryProcess.handleRetry();
                retryRecordList = retryRecordManager.loadNeedRetryRecord(RetryRecordConfig.DEMO, 0);
                log.info("retryProcess.handleRetry times:{}", count++);
            }

            throw UnifiedException.gen("回滚测试数据");
        });
    }

    enum RetryRecordConfig implements IMetaConfig {

        DEMO("1", "商品变更");

        @Getter
        private String bizType;
        @Getter
        private String bizName;

        RetryRecordConfig(String bizType, String bizName) {
            this.bizType = bizType;
            this.bizName = bizName;
        }

        @Override
        public String getAlertType() {
            return "log";
        }

        @Override
        public String getAlertTarget() {
            return "alter target";
        }

        @Override
        public String getTableName() {
            return "retry_record";
        }
    }

}
