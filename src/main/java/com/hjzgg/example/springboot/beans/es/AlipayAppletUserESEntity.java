package com.hjzgg.example.springboot.beans.es;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(indexName = "alipay-applet-storage", type = "alipay-applet-user")
public class AlipayAppletUserESEntity extends BaseESEntity {
    @JsonInclude(Include.NON_NULL)
    private String nickName; //支付宝用户昵称
    @JsonInclude(Include.NON_NULL)
    private String telephone; //手机号码
    @JsonInclude(Include.NON_NULL)
    private String subscribeStatus; // 关注状态：0-关注生活号，绑定小程序；1-关注生活号，没有绑定小程序；2-没有关注生活号，也没有绑定小程序
    @JsonInclude(Include.NON_NULL)
    private String city;
    @JsonInclude(Include.NON_NULL)
    private String province;
    @JsonInclude(Include.NON_NULL)
    private String provinceCode;
    @JsonInclude(Include.NON_NULL)
    private String phoneStatus;
    @JsonInclude(Include.NON_NULL)
    private String avatarUrl;
    @JsonInclude(Include.NON_NULL)
    private String gender;
    @JsonInclude(Include.NON_NULL)
    private String bindPhoneTime;
    @JsonInclude(Include.NON_NULL)
    private String subscribeTime;
    @JsonInclude(Include.NON_NULL)
    private String subscribeTimeloc;
    @JsonInclude(Include.NON_NULL)
    private String unsubscribeTime;
    @JsonInclude(Include.NON_NULL)
    private String subLifestyleStatus; //生活号的关注绑定状态：0-关注生活号，绑定手机号；1-关注生活号，没有绑定手机号；2-没有关注生活号，没有绑定手机号

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSubscribeStatus() {
        return subscribeStatus;
    }

    public void setSubscribeStatus(String subscribeStatus) {
        this.subscribeStatus = subscribeStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getPhoneStatus() {
        return phoneStatus;
    }

    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBindPhoneTime() {
        return bindPhoneTime;
    }

    public void setBindPhoneTime(String bindPhoneTime) {
        this.bindPhoneTime = bindPhoneTime;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getSubscribeTimeloc() {
        return subscribeTimeloc;
    }

    public void setSubscribeTimeloc(String subscribeTimeloc) {
        this.subscribeTimeloc = subscribeTimeloc;
    }

    public String getUnsubscribeTime() {
        return unsubscribeTime;
    }

    public void setUnsubscribeTime(String unsubscribeTime) {
        this.unsubscribeTime = unsubscribeTime;
    }

    public String getSubLifestyleStatus() {
        return subLifestyleStatus;
    }

    public void setSubLifestyleStatus(String subLifestyleStatus) {
        this.subLifestyleStatus = subLifestyleStatus;
    }
}
