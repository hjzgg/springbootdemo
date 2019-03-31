package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class ZookeeperConfigProperties {
    private static String SYS_NAME;
    private static String APP_NAME;
    private static String GROUP_ID;

    static {
        SYS_NAME = System.getProperty("cmos.system.id");
        APP_NAME = System.getProperty("cmos.app.id");
        GROUP_ID = System.getProperty("groupenv");
    }

    ZookeeperConfigProperties() {
        Assert.isTrue(!StringUtils.isAnyBlank(SYS_NAME, APP_NAME, GROUP_ID)
                , "检测WMH配置中心环境变量[cmos.system.id，cmos.app.id，groupenv]是否正确配置");
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
