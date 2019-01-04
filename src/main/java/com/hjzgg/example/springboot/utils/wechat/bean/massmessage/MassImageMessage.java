package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassImageMessage
        extends MassMessage {
    private Map<String, String> image;

    public MassImageMessage(String media_id) {
        this.image = Maps.newHashMap();
        this.image.put("media_id", media_id);
        this.msgtype = "image";
    }

    public Map<String, String> getImage() {
        return this.image;
    }

    public void setImage(Map<String, String> image) {
        this.image = image;
    }
}
