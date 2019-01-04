package com.hjzgg.example.springboot.utils.wechat.bean.user;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

import java.util.List;

public class UserInfoList
        extends BaseResult {
    private List<User> user_info_list;

    public List<User> getUser_info_list() {
        return this.user_info_list;
    }

    public void setUser_info_list(List<User> user_info_list) {
        this.user_info_list = user_info_list;
    }
}
