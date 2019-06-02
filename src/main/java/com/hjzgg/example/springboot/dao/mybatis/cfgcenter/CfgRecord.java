package com.hjzgg.example.springboot.dao.mybatis.cfgcenter;

import com.hjzgg.example.springboot.cfgcenter.annotation.TableField;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CfgRecord {
    private Long id;
    @TableField
    private String systemId;
    @TableField
    private String appId;
    @TableField
    private String groupId;
    @TableField
    private String cfgId;
    @TableField
    private String cfgContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCfgContent() {
        return cfgContent;
    }

    public void setCfgContent(String cfgContent) {
        this.cfgContent = cfgContent;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this
                , ToStringStyle.JSON_STYLE
                , false
                , false
                , CfgRecord.class);
    }
}
