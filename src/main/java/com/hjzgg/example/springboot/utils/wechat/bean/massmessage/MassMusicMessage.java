package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassMusicMessage
        extends MassMessage {
    private Map<String, String> music;

    public MassMusicMessage(String media_id) {
        this.music = Maps.newHashMap();
        this.music.put("media_id", media_id);
        this.msgtype = "music";
    }

    public Map<String, String> getMusic() {
        return this.music;
    }

    public void setMusic(Map<String, String> music) {
        this.music = music;
    }
}
