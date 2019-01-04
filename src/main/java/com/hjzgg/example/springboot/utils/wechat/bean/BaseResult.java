package com.hjzgg.example.springboot.utils.wechat.bean;

import java.io.Serializable;

public class BaseResult
        implements Serializable {
    private static final long serialVersionUID = -1L;
    private static final Integer SUCCESS_CODE = Integer.valueOf(0);
    private Integer errcode;
    private String errmsg;

    public Integer getErrcode() {
        return this.errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = ErrCode.errMsg(this.errcode);
    }

    public boolean isSuccess() {
        return (this.errcode == null) || (this.errcode.equals(SUCCESS_CODE));
    }

    @Override
    public String toString() {
        return "{errcode=" + this.errcode + ", errmsg='" + this.errmsg + '\'' + '}';
    }
}
