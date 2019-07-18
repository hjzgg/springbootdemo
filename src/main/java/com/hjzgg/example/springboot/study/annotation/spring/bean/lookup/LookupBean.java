package com.hjzgg.example.springboot.study.annotation.spring.bean.lookup;

import javax.annotation.PostConstruct;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:49
 **/
public class LookupBean {

    @PostConstruct
    public void init() {
        System.out.println("LookupBean init");
    }

    public void sayHello() {
        System.out.println("LookupBean Hello World!");
    }
}