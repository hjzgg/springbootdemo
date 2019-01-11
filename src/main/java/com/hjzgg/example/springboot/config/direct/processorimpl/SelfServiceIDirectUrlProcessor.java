package com.hjzgg.example.springboot.config.direct.processorimpl;

import com.hjzgg.example.springboot.config.direct.iprocessor.AbstractIDirectUrlProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.net.URI;

/**
 * 自助服务直达查询
 */
@Component
public class SelfServiceIDirectUrlProcessor extends AbstractIDirectUrlProcessor {

    private static final String CONDITION_PATH = "/alipay-applet/direct";

    private void buildQueryAndPath(UriComponentsBuilder uriComponentsBuilder, AlipayAppletUser userInfo) {
        uriComponentsBuilder.path("/" + userInfo.getTelephone())
                .queryParam("channel", "10008")
                .queryParam("uid", userInfo.getUserId())
                .queryParam("provinceid", userInfo.getProvinceCode());
    }

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) throws Exception {
        String userId = "XXXXXXX";
        AlipayAppletUser userInfo = new AlipayAppletUser();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:8080/YYY"
                + request.getServletPath().replace(CONDITION_PATH, StringUtils.EMPTY));

        if (StringUtils.isNotBlank(request.getQueryString())) {
            uriComponentsBuilder.query(request.getQueryString());
        }

        this.buildQueryAndPath(uriComponentsBuilder, userInfo);

        String url = uriComponentsBuilder.build().toUriString();
        URI uri = URI.create(url);
        return handleRestfulCore(request, uri, userId);
    }

    @Override
    public boolean support(HttpServletRequest request) {
        return request.getServletPath().contains(CONDITION_PATH);
    }

    public static class AlipayAppletUser implements Serializable {
        private String userId; //支付宝用户userId
        private String nickName; //支付宝用户昵称
        private String telephone; //手机号码
        private String subscribeStatus; // 关注状态：0-关注生活号，绑定小程序；1-关注生活号，没有绑定小程序；2-没有关注生活号，也没有绑定小程序
        private String city; // 城市名称
        private String province; // 省份名称
        private String provinceCode; // 省份编码
        private String phoneStatus; // 手机绑定状态 默认0-未绑定,1-已绑定,2-解绑
        private String avatarUrl; // 头像url
        private String gender; // 性别
        private String bindPhoneTime; // 绑定手机号的时间
        private String updateTime; // 更新信息时间
        private String subscribeTime; // 关注时间
        private String subscribeTimeloc; // 最近关注时间
        private String unsubscribeTime; // 解除关注时间
        private String subLifestyleStatus; //生活号的关注绑定状态：0-关注生活号，绑定手机号；1-关注生活号，没有绑定手机号；2-没有关注生活号，没有绑定手机号

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

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

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
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

        @Override
        public String toString() {
            return "AlipayAppletUser{" +
                    "userId='" + userId + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", telephone='" + telephone + '\'' +
                    ", subscribe='" + subscribeStatus + '\'' +
                    ", city='" + city + '\'' +
                    ", province='" + province + '\'' +
                    ", provinceCode='" + provinceCode + '\'' +
                    ", phoneStatus='" + phoneStatus + '\'' +
                    ", avatarUrl='" + avatarUrl + '\'' +
                    ", gender='" + gender + '\'' +
                    ", bindPhoneTime='" + bindPhoneTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", subscribeTime='" + subscribeTime + '\'' +
                    ", subscribeTimeLoc='" + subscribeTimeloc + '\'' +
                    ", unsubscribeTime='" + unsubscribeTime + '\'' +
                    '}';
        }
    }

}
