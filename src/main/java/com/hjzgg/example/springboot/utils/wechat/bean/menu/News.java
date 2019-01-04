package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import java.io.Serializable;

public class News
        implements Serializable {
    private String title;
    private String author;
    private String digest;
    private Integer show_cover;
    private String cover_url;
    private String content_url;
    private String source_url;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDigest() {
        return this.digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Integer getShow_cover() {
        return this.show_cover;
    }

    public void setShow_cover(Integer show_cover) {
        this.show_cover = show_cover;
    }

    public String getCover_url() {
        return this.cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getContent_url() {
        return this.content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getSource_url() {
        return this.source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
}
