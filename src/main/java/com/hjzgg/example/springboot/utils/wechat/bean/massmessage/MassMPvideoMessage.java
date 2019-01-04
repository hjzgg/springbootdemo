package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassMPvideoMessage
        extends MassMessage {
    private Map<String, String> mpvideo;

    public MassMPvideoMessage(String media_id) {
        this.mpvideo = Maps.newHashMap();
        this.mpvideo.put("media_id", media_id);
        this.msgtype = "mpvideo";
    }

    public Map<String, String> getMpvideo() {
        return this.mpvideo;
    }

    public void setMpvideo(Map<String, String> mpvideo) {
        this.mpvideo = mpvideo;
    }
}
