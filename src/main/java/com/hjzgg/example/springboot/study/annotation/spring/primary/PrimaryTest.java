package com.hjzgg.example.springboot.study.annotation.spring.primary;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:05
 **/

public class PrimaryTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrimaryConfig.class);
    }
}