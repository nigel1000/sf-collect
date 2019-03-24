package com.common.collect.model.retry.mapper;

import com.common.collect.model.retry.IMetaConfig;
import com.common.collect.model.retry.RetryRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by hznijianfeng on 2018/8/30.
 */

public interface RetryRecordMapper {

    @Options(useGeneratedKeys = true, keyProperty = "retryRecord.id")
    @Insert("insert into ${metaConfig.tableName} "
            + "(id,biz_type,msg_type,msg_key,biz_id,extra,body,try_times,max_try_times,init_error_message,end_error_message,status,create_at,update_at) "
            + "values "
            + "(null,#{retryRecord.bizType},#{retryRecord.msgType},#{retryRecord.msgKey},#{retryRecord.bizId},#{retryRecord.extra},#{retryRecord.body},#{retryRecord.tryTimes},#{retryRecord.maxTryTimes},#{retryRecord.initErrorMessage},#{retryRecord.endErrorMessage},#{retryRecord.status},now(),now())")
    Integer create(@Param("retryRecord") RetryRecord retryRecord, @Param("metaConfig") IMetaConfig metaConfig);

    @Select("select * from ${metaConfig.tableName} "
            + "where msg_key=#{metaConfig.msgKey} and msg_type=#{metaConfig.msgType} and biz_type=#{metaConfig.bizType} "
            + "and status=0 and try_times<=max_try_times")
    List<RetryRecord> loadNeedRetryRecord(@Param("metaConfig") IMetaConfig metaConfig);

    @Select("select * from ${metaConfig.tableName} "
            + "where msg_key=#{metaConfig.msgKey} and msg_type=#{metaConfig.msgType} and biz_type=#{metaConfig.bizType} "
            + "and status=0 and try_times<=max_try_times and biz_id=#{bizId}")
    List<RetryRecord> loadNeedRetryRecordByBizId(@Param("bizId") String bizId,
                                                 @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set status=0,try_times=try_times+1,end_error_message='' where id=#{id}")
    Integer fail(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set status=0, end_error_message=#{errorMessage} ,try_times=try_times+1 where id=#{id}")
    Integer failExp(@Param("id") Long id, @Param("errorMessage") String errorMessage, @Param("metaConfig") IMetaConfig metaConfig);

    @Update("update ${metaConfig.tableName} set status=1 where id=#{id}")
    Integer success(@Param("id") Long id, @Param("metaConfig") IMetaConfig metaConfig);

}
