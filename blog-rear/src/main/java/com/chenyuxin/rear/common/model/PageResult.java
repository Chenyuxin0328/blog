package com.chenyuxin.rear.common.model;

import lombok.Data;

import java.util.List;

/**
 * 通用分页响应封装类
 */
@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页条数
     */
    private long pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据
     */
    private List<T> records;

    public PageResult(long pageNum, long pageSize, long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
    }
}
