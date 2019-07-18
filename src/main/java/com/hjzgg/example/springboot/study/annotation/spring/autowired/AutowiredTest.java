package com.hjzgg.example.springboot.study.annotation.spring.autowired;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:05
 **/

public class AutowiredTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutowiredConfig.class);
        ac.getBeansOfType(AutowiredBean.class)
                .forEach((name, bean) -> System.out.println(name + " " + bean));
    }
}