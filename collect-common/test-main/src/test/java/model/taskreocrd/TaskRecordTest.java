package model.taskreocrd;

import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.EmptyUtil;
import com.common.collect.lib.util.FunctionUtil;
import com.common.collect.model.taskrecord.AbstractTaskProcess;
import com.common.collect.model.taskrecord.IMetaConfig;
import com.common.collect.model.taskrecord.TaskRecordManager;
import com.common.collect.model.taskrecord.infrastructure.TaskRecord;
import com.common.collect.model.taskrecord.infrastructure.TaskRecordMapperExt;
import com.common.collect.tool.mybatis.MybatisContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class TaskRecordTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");
        TaskRecordManager taskRecordManager = (TaskRecordManager) applicationContext.getBean("taskRecordManager");

        taskRecordManager.record(TaskRecordConfig.DEMO, "1", "body", 3, UnifiedException.gen("exception"));
        List<TaskRecord> taskRecordList = taskRecordManager.loadNeedTaskRecord(TaskRecordConfig.DEMO, 0);
        List<Long> ids = FunctionUtil.valueList(taskRecordList, TaskRecord::getId);
        log.info("loadNeedTaskRecord -> return:{}", taskRecordList);
        AbstractTaskProcess taskProcess = new AbstractTaskProcess() {
            @Override
            public IMetaConfig metaConfig() {
                return TaskRecordConfig.DEMO;
            }

            @Override
            public boolean bizExecute(TaskRecord taskRecord) throws Exception {
                return false;
            }
        };
        int count = 1;
        while (EmptyUtil.isNotEmpty(taskRecordList)) {
            taskProcess.handleTask();
            taskRecordList = taskRecordManager.loadNeedTaskRecord(TaskRecordConfig.DEMO, 0);
            if (EmptyUtil.isNotEmpty(taskRecordList)) {
                log.info("taskProcess.handleTask times:{}", count++);
            }
        }

        TaskRecordMapperExt taskRecordMapperExt = (TaskRecordMapperExt) applicationContext.getBean("taskRecordMapperExt");
        MybatisContext.setEnableSqlRecord(true);
        taskRecordMapperExt.deletes(ids);
        log.info("sql -> {}", MybatisContext.getSqlRecord(true));
    }

    enum TaskRecordConfig implements IMetaConfig {

        DEMO("1", "商品变更");

        @Getter
        private String bizType;
        @Getter
        private String bizName;

        TaskRecordConfig(String bizType, String bizName) {
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
            return "task_record";
        }
    }

}
