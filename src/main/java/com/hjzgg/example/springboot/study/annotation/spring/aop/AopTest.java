package com.hjzgg.example.springboot.study.annotation.spring.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:05
 **/

public class AopTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AopConfig.class);
        AopBean aopBean = ac.getBean(AopBean.class);
        aopBean.before();
        System.out.println();
        aopBean.after();
        System.out.println();
        aopBean.test("Hello");
    }
}