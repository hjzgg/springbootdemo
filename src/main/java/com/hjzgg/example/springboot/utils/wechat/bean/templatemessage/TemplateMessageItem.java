package com.hjzgg.example.springboot.utils.wechat.bean.templatemessage;

public class TemplateMessageItem {
    private String value;
    private String color;

    public TemplateMessageItem(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
