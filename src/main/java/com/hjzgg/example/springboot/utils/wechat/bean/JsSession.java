package com.hjzgg.example.springboot.utils.wechat.bean;

public class JsSession
        extends BaseResult {
    private String openid;
    private String session_key;

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSession_key() {
        return this.session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String toString() {
        return super.toString() + "JsSession{openid='" + this.openid + '\'' + ", session_key='" + this.session_key + '\'' + '}';
    }
}
