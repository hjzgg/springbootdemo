package com.hjzgg.example.springboot.utils.wechat.bean.message;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

public class MessageSendResult
        extends BaseResult {
    private String type;
    private String msg_id;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg_id() {
        return this.msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }
}
