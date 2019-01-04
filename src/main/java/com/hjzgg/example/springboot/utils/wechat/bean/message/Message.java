package com.hjzgg.example.springboot.utils.wechat.bean.message;

public abstract class Message {
    private String touser;
    private String msgtype;

    public Message() {
    }

    protected Message(String touser, String msgtype) {
        this.touser = touser;
        this.msgtype = msgtype;
    }

    public String getTouser() {
        return this.touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    protected void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
