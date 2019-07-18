package com.hjzgg.example.springboot.study.annotation.spring.bean.lazy;

import javax.annotation.PostConstruct;

/**
 * @author hujunzheng
 * @create 2019-07-15 23:32
 **/
public class LazyBean {
    @PostConstruct
    public void init() {
        System.out.println("LazyBean init");
    }

    public void sayHello() {
        System.out.println("LazyBean Hello World!");
    }
}