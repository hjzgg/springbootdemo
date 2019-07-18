package com.hjzgg.example.springboot.study.annotation.spring.resource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:05
 **/

public class ResourceTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ResourceConfig.class);
        ac.getBeansOfType(ResourceBean.class)
                .forEach((name, bean) -> System.out.println(name + " " + bean));
    }
}