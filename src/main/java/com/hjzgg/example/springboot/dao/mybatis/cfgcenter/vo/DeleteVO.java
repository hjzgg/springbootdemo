package com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;

public class DeleteVO {
    @NotEmpty
    private String systemId;
    @NotEmpty
    private String appId;

    private String groupId;
    private String cfgId;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCfgId() {
        return cfgId;
    }

    public void setCfgId(String cfgId) {
        this.cfgId = cfgId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this
                , ToStringStyle.JSON_STYLE
                , false
                , false
                , DeleteVO.class);
    }
}
