package com.common.collect.api.page;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nijianfeng on 2018/8/14.
 */

@Getter
public class PageResult<T> implements Serializable {

    /**
     * 分页详细
     */
    private Page page;
    /**
     * 分页数据
     */
    private List<T> records;

    @Data
    @Accessors(chain = true)
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

        /**
         * 游标，默认返回本页列表最后一条数据的 id 值
         */
        private String cursor;

        public static Page ofTotal(Integer total) {
            return new Page().setTotal(total);
        }
    }

    public PageResult() {
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data) {
        this.records = data;
        this.page = Page.ofTotal(total).setCurrentRecordCount(data.size());
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data, @NonNull PageParam pageParam) {
        this.records = data;
        Integer pageSize = pageParam.getPageSize();
        Integer maxPageNo;
        if (total <= pageSize) {
            maxPageNo = 1;
        } else if (total % pageSize == 0) {
            maxPageNo = total / pageSize;
        } else {
            maxPageNo = (total / pageSize) + 1;
        }
        this.page = Page.ofTotal(total)
                .setCurrentRecordCount(data.size())
                .setPageParam(pageParam)
                .setMaxPageNo(maxPageNo)
        ;
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

}
