package com.hjzgg.example.springboot.utils.wechat.bean;

public class XMLTextMessage
        extends XMLMessage {
    private String content;

    public XMLTextMessage(String toUserName, String fromUserName, String content) {
        super(toUserName, fromUserName, "text");
        this.content = content;
    }

    @Override
    public String subXML() {
        return "<Content><![CDATA[" + this.content + "]]></Content>";
    }
}
