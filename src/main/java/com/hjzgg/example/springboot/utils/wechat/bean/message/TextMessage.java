package com.hjzgg.example.springboot.utils.wechat.bean.message;

public class TextMessage
        extends Message {
    private Text text;

    public TextMessage(String touser) {
        super(touser, "text");
    }

    public TextMessage(String touser, String content) {
        this(touser);
        this.text = new Text();
        this.text.setContent(content);
    }

    public Text getText() {
        return this.text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public static class Text {
        private String content;

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
