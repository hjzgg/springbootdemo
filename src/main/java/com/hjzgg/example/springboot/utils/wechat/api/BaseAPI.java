package com.hjzgg.example.springboot.utils.wechat.api;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;

public abstract class BaseAPI {
    protected static final String ACCESS_TOKE = "access_token";
    protected static final String OPEN_ID = "openid";
    protected static final String UTF_8 = "utf-8";
    protected static final String BASE_URI = "https://api.weixin.qq.com";
    protected static final String MEDIA_URI = "http://file.api.weixin.qq.com";
    protected static final String QRCODE_DOWNLOAD_URI = "https://mp.weixin.qq.com";
    protected static final String MCH_URI = "https://api.mch.weixin.qq.com";
    protected static final String OPEN_URI = "https://open.weixin.qq.com";
    protected static Header jsonHeader = new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
    protected static Header xmlHeader = new BasicHeader("Content-Type", ContentType.APPLICATION_XML.toString());
}
