package com.common.collect.lib.api.page;

import com.common.collect.lib.api.docs.DocsDataType;
import com.common.collect.lib.api.docs.DocsFieldExclude;
import com.common.collect.lib.api.excps.UnifiedException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by nijianfeng on 2018/8/14.
 */

@NoArgsConstructor
@Data
@DocsDataType(desc = "分页入参模型")
public class PageParam implements Serializable {
    private static final long serialVersionUID = 4340348427537645063L;

    private Integer pageNo;
    private Integer pageSize;
    private Integer offset;
    private Integer limit;
    private String sortBy;

    @DocsFieldExclude
    private int defaultPageNo = 1; /* 页码默认从1开始 */
    @DocsFieldExclude
    private int defaultOffset = 0; /* mysql默认偏移量从0开始 */
    @DocsFieldExclude
    private int defaultPageSize = 20;

    // 限制获取最大数量
    @DocsFieldExclude
    private Long maxTotal = Long.MAX_VALUE;

    public static PageParam valueOfByLimit(int offset, int limit) throws RuntimeException {
        PageParam pageParam = new PageParam();
        pageParam.init(offset / limit + 1, limit);
        return pageParam;
    }

    public static PageParam valueOfByLimit(int offset, int limit, String sortBy) throws RuntimeException {
        PageParam pageParam = valueOfByLimit(offset, limit);
        pageParam.sortBy = sortBy;
        return pageParam;
    }

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
        if (this.pageNo * this.pageSize > maxTotal) {
            throw UnifiedException.gen("当前获取数量大于了最大限额");
        }
    }

}
