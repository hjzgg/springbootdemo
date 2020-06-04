package com.hjzgg.example.springboot.config.es.routing;

import java.util.Map;

public class SearchParameter {
    protected boolean pageable = false;
    private Integer page = 1;   //当前页(默认第一页)
    private Integer pageSize = 10;  //页数
    private String sort;    //排序字段
    private String scrollId;//滚动查询id
    private String sortOrder;    //排序规则
    private Map<String, Object> fields; //条件查询
    private Map<String, Object[]> range;//区间查询参数Map<age, Object[]{23,50}>

    public boolean isPageable() {
        return pageable;
    }

    public void setPageable(boolean pageable) {
        this.pageable = pageable;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object[]> getRange() {
        return range;
    }

    public void setRange(Map<String, Object[]> range) {
        this.range = range;
    }
}