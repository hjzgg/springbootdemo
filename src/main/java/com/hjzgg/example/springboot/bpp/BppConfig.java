package com.hjzgg.example.springboot.bpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:56
 **/
@Configuration
public class BppConfig {

//    @Bean
//    public BeanPostProcessor proxyBpp1() {
//        return new ProxyBpp1();
//    }

//    @Bean
//    public BeanPostProcessor proxyBpp2() {
//        return new ProxyBpp2();
//    }

//    @Bean
//    public BeanPostProcessor proxyBpp3(@Autowired ApplicationContext ac) {
//        return new ProxyBpp3(ac.getAutowireCapableBeanFactory());
//    }

//    @Bean
//    public BeanPostProcessor proxyBpp4(@Autowired ApplicationContext ac) {
//        return new ProxyBpp4(ac.getAutowireCapableBeanFactory());
//    }

    @Bean
    public BeanPostProcessor proxyBpp5(@Autowired ApplicationContext ac) {
        return new ProxyBpp5(ac.getAutowireCapableBeanFactory());
    }


    @Component
    public static class BppTestBean {
        @Autowired
        private BppTestDepBean depBean;

        public void test1() {
            depBean.testDep();
        }

        public void test2() {
            depBean.testDep();
        }

        @TestMethod
        public void test3() {
            depBean.testDep();
        }
    }

    @Component
    public static class BppTestDepBean {
        public void testDep() {
            System.out.println("HEHE");
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface TestMethod {
    }
}