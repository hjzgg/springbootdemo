package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.Ticket;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

public class TicketAPI
        extends BaseAPI {
    public static Ticket ticketGetTicket(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/ticket/getticket").addParameter("access_token", access_token).addParameter("type", "jsapi").build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Ticket.class);
    }

    public static Ticket cardTicket(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/ticket/getticket").addParameter("access_token", access_token).addParameter("type", "wx_card").build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Ticket.class);
    }
}
