package com.hjzgg.example.springboot.study.annotation.spring.primary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

/**
 * @author hujunzheng
 * @create 2019-07-18 23:42
 **/
@Configuration
public class PrimaryConfig {

    private ParentBean cb;

    @Resource
    public void setChildBean(ParentBean cb) {
        this.cb = cb;
        System.out.println("cb 默认装配：" + this.cb);
    }

    @Autowired
    public void setChildBean1(ParentBean childBean) {
        this.cb = childBean;
        System.out.println("cb 默认装配：" + this.cb);
    }


    @Primary
    @Bean
    public ParentBean parentBean() {
        return new ParentBean();
    }

    @Bean
    public ChildBean childBean() {
        return new ChildBean();
    }

    public static class ParentBean {
    }
    public static class ChildBean extends ParentBean {
    }
}