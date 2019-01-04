package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassTextMessage
        extends MassMessage {
    private Map<String, String> text;

    public MassTextMessage(String content) {
        this.text = Maps.newHashMap();
        this.text.put("content", content);
        this.msgtype = "text";
    }

    public Map<String, String> getText() {
        return this.text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }
}
