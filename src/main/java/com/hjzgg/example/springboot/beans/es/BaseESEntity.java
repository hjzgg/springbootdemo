package com.hjzgg.example.springboot.beans.es;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class BaseESEntity {
    private String id;

    @JsonIgnore
    private Long version;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createTime;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
