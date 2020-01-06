package com.common.collect.model.mapper;

import com.common.collect.model.retry.IMetaConfig;
import com.common.collect.model.retry.RetryRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

public interface RetryRecordMapper {

    @Options(useGeneratedKeys = true, keyProperty = "retryRecord.id")
    @Insert("insert into ${metaConfig.tableName} "
            + "(id,biz_type,alert_type,alert_target,biz_id,extra,body,try_times,max_try_times,first_error_message,last_error_message,state,create_at,update_at) "
            + "values "
            + "(null,#{retryRecord.bizType},#{retryRecord.alertType},#{retryRecord.alertTarget},#{retryRecord.bizId},#{retryRecord.extra},#{retryRecord.body},#{retryRecord.tryTimes},#{retryRecord.maxTryTimes},#{retryRecord.firstErrorMessage},#{retryRecord.lastErrorMessage},#{retryRecord.state},now(),now())")
    Integer create(@Param("retryRecord") RetryRecord retryRecord, @Param("metaConfig") IMetaConfig metaConfig);

    @Select("select * from ${metaConfig.tableName} "
            + "where biz_type=#{metaConfig.bizType} "
            + "and state=0 and id>#{startId} and try_times<=max_try_times order by id asc limit 100")
    List<RetryRecord> loadNeedRetryRecord(@Param("metaConfig") IMetaConfig metaConfig, @Param("startId") long startId);

    @Update("update ${metaConfig.tableName} set state=0,try_times=try_times+1,last_error_message='' where id=#{id}")
    Integer fail(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set state=0, last_error_message=#{errorMessage} ,try_times=try_times+1 where id=#{id}")
    Integer failExp(@Param("id") Long id, @Param("errorMessage") String errorMessage,
                    @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set state=1 where id=#{id}")
    Integer success(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

}
