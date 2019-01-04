package com.hjzgg.example.springboot.utils.wechat.bean;

public class Token
        extends BaseResult {
    private String access_token;
    private int expires_in;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String accessToken) {
        this.access_token = accessToken;
    }

    public int getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(int expiresIn) {
        this.expires_in = expiresIn;
    }

    public String toString() {
        return super.toString() + "{access_token='" + this.access_token + '\'' + ", expires_in=" + this.expires_in + '}';
    }
}
