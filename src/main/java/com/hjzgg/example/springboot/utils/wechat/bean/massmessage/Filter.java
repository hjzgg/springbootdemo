package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

public class Filter {
    private boolean is_to_all;
    private String group_id;

    public Filter(boolean is_to_all, String group_id) {
        this.is_to_all = is_to_all;
        this.group_id = group_id;
    }

    public Filter(String group_id) {
        this.group_id = group_id;
    }

    public boolean isIs_to_all() {
        return this.is_to_all;
    }

    public void setIs_to_all(boolean is_to_all) {
        this.is_to_all = is_to_all;
    }

    public String getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }
}
