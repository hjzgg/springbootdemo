package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.File;

public class ZookeeperConfigProperties {
    public static final String SYS_NAME;
    public static final String APP_NAME;
    public static final String GROUP_ID;
    public static final String BASE_BACKUP_DIR;

    static {
        SYS_NAME = System.getProperty("xxx.system.id");
        APP_NAME = System.getProperty("xxx.app.id");
        GROUP_ID = System.getProperty("groupenv");
        BASE_BACKUP_DIR = System.getProperty("user.home") + File.separator + "cfgcache";
    }

    ZookeeperConfigProperties() {
        Assert.isTrue(!StringUtils.isAnyBlank(SYS_NAME, APP_NAME, GROUP_ID)
                , "检测配置中心客户端环境变量[hjzgg.system.id，hjzgg.app.id，groupenv]是否正确配置");
        this.context = String.format("/wmhcfg/projects/%s/%s/%s", SYS_NAME, APP_NAME, GROUP_ID);
    }

    private String context;
    private boolean failFast = true;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }
}
