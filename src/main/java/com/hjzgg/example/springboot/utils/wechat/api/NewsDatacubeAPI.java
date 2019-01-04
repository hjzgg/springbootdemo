package com.hjzgg.example.springboot.utils.wechat.api;

import java.nio.charset.Charset;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

public class NewsDatacubeAPI
        extends BaseAPI {
    public static String getArticleSummary(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getarticlesummary").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }

    public static String getArticleTotal(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getarticletotal").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }

    public static String getUserRead(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getuserread").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }

    public static String getUserReadHour(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getuserreadhour").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }

    public static String getUserShare(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getusershare").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }

    public static String getUserShareHour(String access_token, String begin_date, String end_date) {
        String messageJson = "{\"begin_date\":\"" + begin_date + "\",\"end_date\":\"" + end_date + "\"}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/datacube/getusersharehour").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, String.class);
    }
}
