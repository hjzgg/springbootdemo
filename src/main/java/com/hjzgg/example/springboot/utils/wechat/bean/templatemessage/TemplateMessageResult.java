package com.hjzgg.example.springboot.utils.wechat.bean.templatemessage;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

public class TemplateMessageResult
        extends BaseResult {
    private Long msgid;

    public Long getMsgid() {
        return this.msgid;
    }

    public void setMsgid(Long msgid) {
        this.msgid = msgid;
    }
}
