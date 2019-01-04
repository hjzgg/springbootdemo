package com.hjzgg.example.springboot.utils.wechat.bean.massmessage;

import java.util.Set;

public abstract class MassMessage {
    protected String msgtype;
    private Filter filter;
    private Set<String> touser;

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Set<String> getTouser() {
        return this.touser;
    }

    public void setTouser(Set<String> touser) {
        this.touser = touser;
    }
}
