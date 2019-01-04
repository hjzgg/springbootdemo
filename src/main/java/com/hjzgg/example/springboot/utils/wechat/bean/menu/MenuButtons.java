package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import java.util.List;

public class MenuButtons {
    private Button[] button;

    public Button[] getButton() {
        return this.button;
    }

    public void setButton(Button[] button) {
        this.button = button;
    }

    public static class Button {
        private String type;
        private String name;
        private String key;
        private String url;
        private List<Button> sub_button;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<Button> getSub_button() {
            return this.sub_button;
        }

        public void setSub_button(List<Button> subButton) {
            this.sub_button = subButton;
        }
    }
}
