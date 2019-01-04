package com.hjzgg.example.springboot.utils.wechat.bean;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public abstract class XMLMessage {
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLMessage.class);

    private String toUserName;
    private String fromUserName;
    private String msgType;

    protected XMLMessage(String toUserName, String fromUserName, String msgType) {
        this.toUserName = toUserName;
        this.fromUserName = fromUserName;
        this.msgType = msgType;
    }

    public abstract String subXML();

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA[" + this.toUserName + "]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[" + this.fromUserName + "]]></FromUserName>");
        sb.append("<CreateTime>" + System.currentTimeMillis() / 1000L + "</CreateTime>");
        sb.append("<MsgType><![CDATA[" + this.msgType + "]]></MsgType>");
        sb.append(subXML());
        sb.append("</xml>");
        return sb.toString();
    }

    public boolean outputStreamWrite(OutputStream outputStream) {
        try {
            outputStream.write(toXML().getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }
}
