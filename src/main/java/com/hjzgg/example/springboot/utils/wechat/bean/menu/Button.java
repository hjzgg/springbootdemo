package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import java.io.Serializable;
import java.util.List;

public class Button
        implements Serializable {
    private String name;
    private String type;
    private String key;
    private String url;
    private String value;
    private NewsInfo news_info;
    private List<Button> list;
    private List<Button> sub_button;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Button> getList() {
        return this.list;
    }

    public void setList(List<Button> list) {
        this.list = list;
    }

    public List<Button> getSub_button() {
        return this.sub_button;
    }

    public void setSub_button(List<Button> sub_button) {
        this.sub_button = sub_button;
    }

    public NewsInfo getNews_info() {
        return this.news_info;
    }

    public void setNews_info(NewsInfo news_info) {
        this.news_info = news_info;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
