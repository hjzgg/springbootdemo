package com.hjzgg.example.springboot.utils.wechat.bean;

public class Ticket
        extends BaseResult {
    private String ticket;
    private Integer expires_in;

    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String toString() {
        return super.toString() + "Ticket{ticket='" + this.ticket + '\'' + ", expires_in=" + this.expires_in + '}';
    }
}
