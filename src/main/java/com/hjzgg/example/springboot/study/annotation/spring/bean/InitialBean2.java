package com.hjzgg.example.springboot.study.annotation.spring.bean;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:13
 **/
public class InitialBean2 {
    public void init() {
        System.out.println("InitialBean2 init");
    }

    public void close() {
        System.out.println("InitialBean2 destory");
    }
}