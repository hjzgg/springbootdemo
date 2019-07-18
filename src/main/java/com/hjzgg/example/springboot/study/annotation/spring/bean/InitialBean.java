package com.hjzgg.example.springboot.study.annotation.spring.bean;

import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:13
 **/
public class InitialBean implements InitializingBean {

    @PostConstruct
    public void init() {
        System.out.println("InitialBean init");
    }

    public void sayHello() {
        System.out.println("InitialBean Hello World!");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("InitialBean Initializing");
    }

    @PreDestroy
    public void destory() {
        System.out.println("InitialBean destory");
    }
}