package collect.debug;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.TransactionHelper;
import com.common.collect.model.retry.AbstractRetryProcess;
import com.common.collect.model.retry.IMetaConfig;
import com.common.collect.model.retry.RetryRecord;
import com.common.collect.model.retry.RetryRecordService;
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

        RetryRecordService retryRecordService = (RetryRecordService) applicationContext.getBean("retryRecordService");

        TransactionHelper transactionHelper = (TransactionHelper) applicationContext.getBean("transactionHelper");
        transactionHelper.aroundBiz(() -> {
            retryRecordService.record(RetryRecord.gen("测试", null), RetryRecordConfig.DEMO);

            List<RetryRecord> retryRecordList = retryRecordService.loadNeedRetryRecord(RetryRecordConfig.DEMO);
            log.info("loadNeedRetryRecord -> return:{}", retryRecordList);
            AbstractRetryProcess retryProcess = new AbstractRetryProcess() {
                @Override
                public void init() {
                    this.setMetaConfig(RetryRecordConfig.DEMO);
                    this.setRetryRecordService(retryRecordService);
                }

                @Override
                public void failExecute(RetryRecord retryRecord) {
                    log.error("fail bizExecute retryRecord:{}", retryRecord.getId());
                }

                @Override
                public boolean bizExecute(RetryRecord retryRecord) throws Exception {
                    return false;
                }
            };
            retryProcess.init();

            while (EmptyUtil.isNotEmpty(retryRecordList)) {
                retryProcess.handleRetry();
                retryRecordList = retryRecordService.loadNeedRetryRecord(RetryRecordConfig.DEMO);
                log.info("loadNeedRetryRecord -> return:{}", retryRecordList);
            }

            throw UnifiedException.gen("回滚测试数据");
        });
    }

    enum RetryRecordConfig implements IMetaConfig {

        DEMO("商品变更", "kafka", "topic.gooods");

        @Getter
        private String bizType;
        @Getter
        private String msgType;
        @Getter
        private String msgKey;

        RetryRecordConfig(String bizType, String msgType, String msgKey) {
            this.bizType = bizType;
            this.msgType = msgType;
            this.msgKey = msgKey;
        }

        @Override
        public String getTableName() {
            return "retry_record";
        }
    }

}
