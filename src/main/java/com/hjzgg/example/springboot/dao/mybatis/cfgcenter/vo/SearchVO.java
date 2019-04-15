package com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo;

import org.hibernate.validator.constraints.NotEmpty;

public class SearchVO {
    @NotEmpty
    private String systemId;
    @NotEmpty
    private String appId;
    @NotEmpty
    private String groupId;

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
}
