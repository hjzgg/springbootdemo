package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.Token;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

public class TokenAPI
        extends BaseAPI {
    public static Token token(String appid, String secret) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/token").addParameter("grant_type", "client_credential").addParameter("appid", appid).addParameter("secret", secret).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Token.class);
    }
}
