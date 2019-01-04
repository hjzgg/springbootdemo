package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import com.google.common.collect.Maps;

import java.util.Map;

public class MassVoiceMessage
        extends MassMessage {
    private Map<String, String> voice;

    public MassVoiceMessage(String media_id) {
        this.voice = Maps.newHashMap();
        this.voice.put("media_id", media_id);
        this.msgtype = "voice";
    }

    public Map<String, String> getVoice() {
        return this.voice;
    }

    public void setVoice(Map<String, String> voice) {
        this.voice = voice;
    }
}
