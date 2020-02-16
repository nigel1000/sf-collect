package com.common.collect.model.flowlog.infrastructure;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hznijianfeng on 2020/02/15.
 *
 * 操作日志记录流程表
 */

@Data
@NoArgsConstructor
public class FlowLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private Long id;
    
    /**
     * 操作类型
     */
    private String bizType;
    
    /**
     * 业务 id
     */
    private String bizId;
    
    /**
     * 先前的值 建议转成 json
     */
    private String beforeValue;
    
    /**
     * 修改参数 建议转成 json
     */
    private String updateValue;
    
    /**
     * 修改后的值 建议转成 json
     */
    private String afterValue;
    
    /**
     * 额外的扩展字段，存sql等
     */
    private String extra;
    
    /**
     * 操作备注
     */
    private String operateRemark;
    
    /**
     * 操作人 id
     */
    private String operatorId;
    
    /**
     * 操作人 姓名
     */
    private String operatorName;
    
    /**
     * 创建时间
     */
    private Date createAt;
    
    /**
     * 修改时间
     */
    private Date updateAt;
    

}