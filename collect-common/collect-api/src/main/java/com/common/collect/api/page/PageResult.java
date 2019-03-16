package com.common.collect.api.page;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nijianfeng on 2018/8/14.
 */

@Getter
@ToString
public class PageResult<T> implements Serializable {

    /**
     * 总记录数
     */
    private Integer total;
    /**
     * 数据
     */
    private List<T> data;

    /* 一般前端插件会封装，pageNo和pageSize前端传进来的，pageTotal可以根据total和pageSize算 */
    private PageParam pageParam;
    /**
     * 总页数
     */
    private Integer pageTotal;
    /**
     * 当前页
     */
    private Integer pageNo;
    /**
     * 每页记录数
     */
    private Integer pageSize;

    public PageResult() {
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data) {
        this.data = data;
        this.total = total;
    }

    private PageResult(@NonNull Integer total, @NonNull List<T> data, @NonNull PageParam pageParam) {
        this.data = data;
        this.total = total;
        this.pageParam = pageParam;
        this.pageNo = pageParam.getPageNo();
        this.pageSize = pageParam.getPageSize();
        if (this.total <= this.pageSize) {
            this.pageTotal = 1;
        } else if (this.total % this.pageSize == 0) {
            this.pageTotal = this.total / this.pageSize;
        } else {
            this.pageTotal = (this.total / this.pageSize) + 1;
        }
    }

    public Boolean isEmpty() {
        return Objects.equals(0, this.total) || this.data == null || this.data.isEmpty();
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, new ArrayList<T>());
    }

    public static <T> PageResult<T> gen(Integer total, List<T> data) {
        return new PageResult<>(total, data);
    }

    public static <T> PageResult<T> gen(Integer total, List<T> data, PageParam pageParam) {
        return new PageResult<>(total, data, pageParam);
    }
}
