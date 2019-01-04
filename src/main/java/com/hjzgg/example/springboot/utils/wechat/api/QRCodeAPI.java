package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.QRCode.QRTicket;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

public class QRCodeAPI
        extends BaseAPI {
    public static QRTicket QRCodeGetTicket(String access_token, String actionName, String info) {
        String json = "";
        if ("QR_SCENE".equals(actionName)) {
            json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + info + "}}}";
        } else if ("QR_LIMIT_SCENE".equals(actionName)) {
            json = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": " + info + "}}}";
        } else if ("QR_LIMIT_STR_SCENE".equals(actionName)) {
            json = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"" + info + "\"}}}";
        }
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/cgi-bin/qrcode/create").addParameter("access_token", access_token).setEntity(new StringEntity(json, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, QRTicket.class);
    }
}
