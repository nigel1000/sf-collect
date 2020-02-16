package com.common.collect.lib.api.page;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nijianfeng on 2018/8/14.
 */

@Getter
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 5645629639975335821L;

    /**
     * 分页详细
     */
    private Page page;
    /**
     * 分页数据
     */
    private List<T> records;

    public PageResult() {
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data) {
        this.records = data;
        this.page = Page.builder()
                .total(total)
                .currentRecordCount(data.size())
                .build();
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data, @NonNull PageParam pageParam) {
        this.records = data;
        Integer pageSize = pageParam.getPageSize();
        int maxPageNo;
        if (total <= pageSize) {
            maxPageNo = 1;
        } else if (total % pageSize == 0) {
            maxPageNo = total / pageSize;
        } else {
            maxPageNo = (total / pageSize) + 1;
        }
        this.page = Page.builder()
                .total(total)
                .currentRecordCount(data.size())
                .pageParam(pageParam)
                .maxPageNo(maxPageNo)
                .build();
    }

    public static <T> PageResult<T> gen(Integer total, List<T> data) {
        return new PageResult<>(total, data);
    }

    public static <T> PageResult<T> gen(Integer total, List<T> data, PageParam pageParam) {
        return new PageResult<>(total, data, pageParam);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, new ArrayList<T>());
    }

    public Boolean isEmpty() {
        return Objects.equals(0, this.page.getTotal()) || this.records == null || this.records.isEmpty();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page implements Serializable {
        /**
         * 总记录数
         */
        private Integer total;

        /**
         * 分页数据数量
         */
        private Integer currentRecordCount;

        /**
         * 入参请求
         */
        private PageParam pageParam;

        /**
         * 最大页码
         */
        private Integer maxPageNo;

    }

}
