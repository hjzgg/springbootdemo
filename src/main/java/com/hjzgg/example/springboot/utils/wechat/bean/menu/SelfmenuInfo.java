package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import java.io.Serializable;
import java.util.List;

public class SelfmenuInfo
        implements Serializable {
    private List<Button> button;

    public List<Button> getButton() {
        return this.button;
    }

    public void setButton(List<Button> button) {
        this.button = button;
    }
}
