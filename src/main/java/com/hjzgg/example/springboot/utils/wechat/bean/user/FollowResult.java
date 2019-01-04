package com.hjzgg.example.springboot.utils.wechat.bean.user;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

public class FollowResult
        extends BaseResult {
    private Integer total;
    private Integer count;
    private String next_openid;
    private transient Data data;

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext_openid() {
        return this.next_openid;
    }

    public void setNext_openid(String nextOpenid) {
        this.next_openid = nextOpenid;
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String[] openid;

        public String[] getOpenid() {
            return this.openid;
        }

        public void setOpenid(String[] openid) {
            this.openid = openid;
        }
    }
}
