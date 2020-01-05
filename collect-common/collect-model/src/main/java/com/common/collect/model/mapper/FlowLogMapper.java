package com.common.collect.model.mapper;

import com.common.collect.container.mybatis.BaseMapper;
import com.common.collect.model.flowlog.FlowLog;
import com.common.collect.model.flowlog.IMetaConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * Created by nijianfeng on 2019/3/17.
 */
public interface FlowLogMapper extends BaseMapper<FlowLog> {

    @Options(useGeneratedKeys = true, keyProperty = "flowLog.id")
    @Insert("insert into ${metaConfig.tableName} " +
            "(" +
            "`id`," +
            "`biz_id`," +
            "`biz_type`," +
            "`before_value`," +
            "`update_value`," +
            "`after_value`," +
            "`extra`," +
            "`operate_remark`," +
            "`operator_id`," +
            "`operator_name`," +
            "`create_at`," +
            "`update_at`" +
            ") " +
            "values " +
            "(" +
            "null," +
            "#{flowLog.bizId}," +
            "#{flowLog.bizType}," +
            "#{flowLog.beforeValue}," +
            "#{flowLog.updateValue}," +
            "#{flowLog.afterValue}," +
            "#{flowLog.extra}," +
            "#{flowLog.operateRemark}," +
            "#{flowLog.operatorId}," +
            "#{flowLog.operatorName}," +
            "now()," +
            "now()" +
            ")")
    Integer record(@Param("metaConfig") IMetaConfig metaConfig, @Param("flowLog") FlowLog flowLog);


}
