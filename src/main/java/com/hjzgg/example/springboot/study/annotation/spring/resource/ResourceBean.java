package com.hjzgg.example.springboot.study.annotation.spring.resource;

import javax.annotation.PostConstruct;

/**
 * @author hujunzheng
 * @create 2019-07-18 22:52
 **/
public class ResourceBean {
    @PostConstruct
    public void init() {
        System.out.println("ResourceBean init");
    }
}