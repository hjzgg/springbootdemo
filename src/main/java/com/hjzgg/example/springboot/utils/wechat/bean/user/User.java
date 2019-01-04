package com.hjzgg.example.springboot.utils.wechat.bean.user;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

import java.util.Arrays;

public class User
        extends BaseResult {
    private Integer subscribe;
    private String openid;
    private String nickname;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private Integer subscribe_time;
    private String[] privilege;
    private String unionid;
    private Integer groupid;
    private String remark;

    public Integer getSubscribe() {
        return this.subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return this.headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public Integer getSubscribe_time() {
        return this.subscribe_time;
    }

    public void setSubscribe_time(Integer subscribeTime) {
        this.subscribe_time = subscribeTime;
    }

    public String[] getPrivilege() {
        return this.privilege;
    }

    public void setPrivilege(String[] privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return this.unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Integer getGroupid() {
        return this.groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String toString() {
        return

                "User{subscribe=" + this.subscribe + ", openid='" + this.openid + '\'' + ", nickname='" + this.nickname + '\'' + ", sex=" + this.sex + ", language='" + this.language + '\'' + ", city='" + this.city + '\'' + ", province='" + this.province + '\'' + ", country='" + this.country + '\'' + ", headimgurl='" + this.headimgurl + '\'' + ", subscribe_time=" + this.subscribe_time + ", privilege=" + Arrays.toString(this.privilege) + ", unionid='" + this.unionid + '\'' + ", groupid=" + this.groupid + ", remark='" + this.remark + '\'' + '}';
    }
}
