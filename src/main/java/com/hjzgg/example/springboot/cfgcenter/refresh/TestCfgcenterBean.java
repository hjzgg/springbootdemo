package com.hjzgg.example.springboot.cfgcenter.refresh;

import com.alibaba.fastjson.JSON;
import com.hjzgg.example.springboot.cfgcenter.annotation.ConfigField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author hujunzheng
 * @create 2019-06-02 23:54
 **/
@Component
@ConfigurationProperties(prefix = "cfg.test")
public class TestCfgcenterBean extends BaseCfgcenterBean {

    @ConfigField
    private String text;

    @ConfigField
    private Map<String, List<String>> map;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, List<String>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<String>> map) {
        this.map = map;
    }

    @Override
    protected String getDefaultResourcePath() {
        return StringUtils.EMPTY;
    }

    @Override
    protected void refresh() {
        super.refresh();
        System.out.println("text=" + this.text);

        System.out.println("map=" + JSON.toJSONString(map));
    }
}