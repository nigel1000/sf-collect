package com.common.collect.model.taskrecord.infrastructure;

import com.common.collect.model.taskrecord.IMetaConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

public interface TaskRecordMapperExt extends TaskRecordMapper {

    @Options(useGeneratedKeys = true, keyProperty = "taskRecord.id")
    @Insert("insert into ${metaConfig.tableName} "
            + "(id,biz_type,alert_type,alert_target,biz_id,extra,body,try_times,max_try_times,first_error_message,last_error_message,state,create_at,update_at) "
            + "values "
            + "(null,#{taskRecord.bizType},#{taskRecord.alertType},#{taskRecord.alertTarget},#{taskRecord.bizId},#{taskRecord.extra},#{taskRecord.body},#{taskRecord.tryTimes},#{taskRecord.maxTryTimes},#{taskRecord.firstErrorMessage},#{taskRecord.lastErrorMessage},#{taskRecord.state},now(),now())")
    Integer create(@Param("taskRecord") TaskRecord taskRecord, @Param("metaConfig") IMetaConfig metaConfig);

    @Select("select * from ${metaConfig.tableName} "
            + "where biz_type=#{metaConfig.bizType} "
            + "and state=0 and id>#{startId} and try_times<=max_try_times order by id asc limit 100")
    List<TaskRecord> loadNeedTaskRecord(@Param("metaConfig") IMetaConfig metaConfig, @Param("startId") long startId);

    @Update("update ${metaConfig.tableName} set state=0,try_times=try_times+1,last_error_message='' where id=#{id}")
    Integer fail(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set state=0, last_error_message=#{errorMessage} ,try_times=try_times+1 where id=#{id}")
    Integer failExp(@Param("id") Long id, @Param("errorMessage") String errorMessage,
                    @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set state=1 where id=#{id}")
    Integer success(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

}
