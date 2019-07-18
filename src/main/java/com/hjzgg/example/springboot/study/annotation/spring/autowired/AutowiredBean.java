package com.hjzgg.example.springboot.study.annotation.spring.autowired;

import javax.annotation.PostConstruct;

/**
 * @author hujunzheng
 * @create 2019-07-18 22:36
 **/
public class AutowiredBean {
    @PostConstruct
    public void init() {
        System.out.println("AutowiredBean init");
    }
}