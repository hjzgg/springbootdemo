package com.hjzgg.example.springboot.utils.wechat.bean.message;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

public class Media
        extends BaseResult {
    private String type;
    private String media_id;
    private Integer created_at;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedia_id() {
        return this.media_id;
    }

    public void setMedia_id(String mediaId) {
        this.media_id = mediaId;
    }

    public Integer getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Integer createdAt) {
        this.created_at = createdAt;
    }
}
