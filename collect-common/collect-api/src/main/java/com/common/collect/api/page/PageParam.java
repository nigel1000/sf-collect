package com.common.collect.api.page;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by nijianfeng on 2018/8/14.
 */

@ToString
@NoArgsConstructor
@Data
public class PageParam implements Serializable {

    private Integer pageNo;
    private Integer pageSize;
    private Integer offset;
    private Integer limit;
    private String sortBy;

    private int defaultPageNo = 1; /* 页码默认从1开始 */
    private int defaultOffset = 0; /* mysql默认偏移量从0开始 */
    private int defaultPageSize = 10;

    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.init(pageNo, pageSize);
        return pageParam;
    }

    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize, String sortBy) {
        PageParam pageParam = valueOfByPageNo(pageNo, pageSize);
        pageParam.sortBy = sortBy;
        return pageParam;
    }

    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize, Integer defaultPageSize) {
        PageParam pageParam = new PageParam();
        pageParam.defaultPageSize = defaultPageSize;
        pageParam.init(pageNo, pageSize);
        return pageParam;
    }

    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize, Integer defaultPageSize, String sortBy) {
        PageParam pageParam = valueOfByPageNo(pageNo, pageSize, defaultPageSize);
        pageParam.sortBy = sortBy;
        return pageParam;
    }

    private void init(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        if (null == pageNo || pageNo <= 0) {
            this.pageNo = defaultPageNo;
        }
        if (null == pageSize || pageSize <= 0) {
            this.pageSize = defaultPageSize;
        }
        this.offset = (this.pageNo - 1) * this.pageSize;
        this.limit = this.pageSize;
    }

}
