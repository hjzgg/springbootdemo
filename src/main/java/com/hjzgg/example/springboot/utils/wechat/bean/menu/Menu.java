package com.hjzgg.example.springboot.utils.wechat.bean.menu;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

public class Menu
        extends BaseResult {
    private MenuButtons menu;

    public MenuButtons getMenu() {
        return this.menu;
    }

    public void setMenu(MenuButtons menu) {
        this.menu = menu;
    }
}
