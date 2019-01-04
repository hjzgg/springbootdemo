package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.JsSession;
import com.hjzgg.example.springboot.utils.wechat.bean.SnsToken;
import com.hjzgg.example.springboot.utils.wechat.bean.user.User;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SnsAPI
        extends BaseAPI {
    private static final Logger logger = LoggerFactory.getLogger(SnsAPI.class);

    public static SnsToken oauth2AccessToken(String appid, String secret, String code) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/sns/oauth2/access_token").addParameter("appid", appid).addParameter("secret", secret).addParameter("code", code).addParameter("grant_type", "authorization_code").build();
        return (SnsToken) LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    public static SnsToken oauth2ComponentAccessToken(String appid, String code, String component_appid, String component_access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/sns/oauth2/component/access_token").addParameter("appid", appid).addParameter("code", code).addParameter("grant_type", "authorization_code").addParameter("component_appid", component_appid).addParameter("component_access_token", component_access_token).build();
        return (SnsToken) LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    public static SnsToken oauth2RefreshToken(String appid, String refresh_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/sns/oauth2/refresh_token").addParameter("appid", appid).addParameter("refresh_token", refresh_token).addParameter("grant_type", "refresh_token").build();
        return (SnsToken) LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    public static SnsToken oauth2ComponentRefreshToken(String appid, String refresh_token, String component_appid, String component_access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/sns/oauth2/component/refresh_token").addParameter("appid", appid).addParameter("refresh_token", refresh_token).addParameter("grant_type", "refresh_token").addParameter("component_appid", component_appid).addParameter("component_access_token", component_access_token).build();
        return (SnsToken) LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    public static User userinfo(String access_token, String openid, String lang) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/sns/userinfo").addParameter("access_token", access_token).addParameter("openid", openid).addParameter("lang", lang).build();
        return (User) LocalHttpClient.executeJsonResult(httpUriRequest, User.class);
    }

    public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo, String state) {
        return connectOauth2Authorize(appid, redirect_uri, snsapi_userinfo, state, null);
    }

    public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo, String state, String component_appid) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?")
                    .append("appid=").append(appid)
                    .append("&redirect_uri=").append(URLEncoder.encode(redirect_uri, "utf-8"))
                    .append("&response_type=code")
                    .append("&scope=").append(snsapi_userinfo ? "snsapi_userinfo" : "snsapi_base")
                    .append("&state=").append(state == null ? "STATE" : state);
            if (component_appid != null) {
                sb.append("&component_appid=").append(component_appid);
            }
            sb.append("#wechat_redirect");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        return null;
    }

    public static JsSession jscode2Session(String appid, String secret, String js_code) {
        HttpUriRequest request = RequestBuilder.get().setUri("https://api.weixin.qq.com/sns/jscode2session").addParameter("appid", appid).addParameter("secret", secret).addParameter("js_code", js_code).addParameter("grant_type", "authorization_code").build();
        return LocalHttpClient.executeJsonResult(request, JsSession.class);
    }
}
