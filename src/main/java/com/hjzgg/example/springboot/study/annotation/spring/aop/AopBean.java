package com.hjzgg.example.springboot.study.annotation.spring.aop;

import com.hjzgg.example.springboot.utils.sign.Sign;
import org.springframework.stereotype.Component;

/**
 * @author hujunzheng
 * @create 2019-07-19 0:10
 **/
@Component
public class AopBean {

    @Sign
    public void before() {
        System.out.println("AopBean before 方法");
    }

    @Sign
    public void after() {
        System.out.println("AopBean after 方法");
    }

    @Sign
    public void test(String content) {
        System.out.println("AopBean test 方法 ---> " + content);
    }
}