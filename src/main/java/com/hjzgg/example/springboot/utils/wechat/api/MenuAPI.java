package com.hjzgg.example.springboot.utils.wechat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hjzgg.example.springboot.utils.JacksonHelper;
import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;
import com.hjzgg.example.springboot.utils.wechat.bean.menu.CurrentSelfmenuInfo;
import com.hjzgg.example.springboot.utils.wechat.bean.menu.Menu;
import com.hjzgg.example.springboot.utils.wechat.bean.menu.MenuButtons;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;

public class MenuAPI extends BaseAPI {
    public static BaseResult menuCreate(String access_token, String menuJson) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/menu/create").addParameter("access_token", access_token).setEntity(new StringEntity(menuJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static BaseResult menuCreate(String access_token, MenuButtons menuButtons) {
        String str = JacksonHelper.toJson(menuButtons);
        return menuCreate(access_token, str);
    }

    public static Menu menuGet(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/menu/get").addParameter("access_token", access_token).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Menu.class);
    }

    public static BaseResult menuDelete(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/cgi-bin/menu/delete").addParameter("access_token", access_token).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static CurrentSelfmenuInfo get_current_selfmenu_info(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info").addParameter("access_token", access_token).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, CurrentSelfmenuInfo.class);
    }
}
