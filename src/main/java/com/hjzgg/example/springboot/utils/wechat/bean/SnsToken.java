package com.hjzgg.example.springboot.utils.wechat.bean;

public class SnsToken
        extends BaseResult {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String accessToken) {
        this.access_token = accessToken;
    }

    public Integer getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(Integer expiresIn) {
        this.expires_in = expiresIn;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String toString() {
        return super.toString() + "{access_token='" + this.access_token + '\'' + ", expires_in=" + this.expires_in + ", refresh_token='" + this.refresh_token + '\'' + ", openid='" + this.openid + '\'' + ", scope='" + this.scope + '\'' + '}';
    }
}
