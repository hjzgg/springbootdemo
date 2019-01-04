package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import java.io.Serializable;
import java.util.List;

public class NewsInfo
        implements Serializable {
    private List<News> list;

    public List<News> getList() {
        return this.list;
    }

    public void setList(List<News> list) {
        this.list = list;
    }
}
