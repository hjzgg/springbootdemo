package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassMPnewsMessage
        extends MassMessage {
    private Map<String, String> mpnews;

    public MassMPnewsMessage(String media_id) {
        this.mpnews = Maps.newHashMap();
        this.mpnews.put("media_id", media_id);
        this.msgtype = "mpnews";
    }

    public Map<String, String> getMpnews() {
        return this.mpnews;
    }

    public void setMpnews(Map<String, String> mpnews) {
        this.mpnews = mpnews;
    }
}
