package com.hjzgg.example.springboot.utils.wechat.bean.message;

public class VoiceMessage
        extends Message {
    public Voice voice;

    public VoiceMessage(String touser, String mediaId) {
        super(touser, "voice");
        this.voice = new Voice();
        this.voice.setMedia_id(mediaId);
    }

    public Voice getVoice() {
        return this.voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public static class Voice {
        private String media_id;

        public String getMedia_id() {
            return this.media_id;
        }

        public void setMedia_id(String mediaId) {
            this.media_id = mediaId;
        }
    }
}
