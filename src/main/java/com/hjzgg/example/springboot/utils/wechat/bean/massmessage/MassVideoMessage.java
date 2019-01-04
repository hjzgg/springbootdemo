package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;


import com.hjzgg.example.springboot.utils.wechat.bean.message.Uploadvideo;

public class MassVideoMessage
        extends MassMessage {
    private Uploadvideo video;

    public MassVideoMessage(Uploadvideo uploadvideo) {
        this.video = uploadvideo;
        this.msgtype = "video";
    }

    public Uploadvideo getVideo() {
        return this.video;
    }

    public void setVideo(Uploadvideo video) {
        this.video = video;
    }
}
