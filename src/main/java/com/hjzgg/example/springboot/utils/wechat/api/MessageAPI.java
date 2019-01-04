package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.JacksonHelper;
import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;
import com.hjzgg.example.springboot.utils.wechat.bean.massmessage.MassMessage;
import com.hjzgg.example.springboot.utils.wechat.bean.message.*;
import com.hjzgg.example.springboot.utils.wechat.bean.templatemessage.TemplateMessage;
import com.hjzgg.example.springboot.utils.wechat.bean.templatemessage.TemplateMessageResult;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;
import java.util.List;

public class MessageAPI
        extends BaseAPI {
    public static BaseResult messageCustomSend(String access_token, String messageJson) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/message/custom/send").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static BaseResult messageCustomSend(String access_token, Message message) {
        String str = JacksonHelper.toJson(message);
        return messageCustomSend(access_token, str);
    }

    public static Media mediaUploadnews(String access_token, List<NewsMessage.Article> articles) {
        String str = JacksonHelper.toJson(articles);
        String messageJson = "{\"articles\":" + str + "}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/media/uploadnews").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Media.class);
    }

    public static Media mediaUploadvideo(String access_token, Uploadvideo uploadvideo) {
        String messageJson = JacksonHelper.toJson(uploadvideo);

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("http://file.api.weixin.qq.com/cgi-bin/media/uploadvideo").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Media.class);
    }

    public static MessageSendResult messageMassSendall(String access_token, String messageJson) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/message/mass/sendall").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, MessageSendResult.class);
    }

    public static MessageSendResult messageMassSendall(String access_token, MassMessage massMessage) {
        String str = JacksonHelper.toJson(massMessage);
        return messageMassSendall(access_token, str);
    }

    public static MessageSendResult messageMassSend(String access_token, String messageJson) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/message/mass/send").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, MessageSendResult.class);
    }

    public static MessageSendResult messageMassSend(String access_token, MassMessage massMessage) {
        String str = JacksonHelper.toJson(massMessage);
        return messageMassSend(access_token, str);
    }

    public static BaseResult messageMassDelete(String access_token, String msgid) {
        String messageJson = "{\"msgid\":" + msgid + "}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/message/mass/delete").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static TemplateMessageResult messageTemplateSend(String access_token, TemplateMessage templateMessage) {
        String messageJson = JacksonHelper.toJson(templateMessage);

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/message/template/send").addParameter("access_token", access_token).setEntity(new StringEntity(messageJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, TemplateMessageResult.class);
    }
}
