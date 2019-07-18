package com.hjzgg.example.springboot.study.annotation.spring.bean.lazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author hujunzheng
 * @create 2019-07-16 23:08
 **/
@Configuration
public class LazyBeanConfig {

    private LazyBeanA lazyBeanA;

    @Autowired
    public void setLazyBeanA(LazyBeanA lazyBeanA) {
        this.lazyBeanA = lazyBeanA;
    }

    @Bean
    public LazyBeanA lazyBeanA() {
        return new LazyBeanA();
    }

    @Bean
    public LazyBeanB lazyBeanB() {
        return new LazyBeanB();
    }

    @Lazy
    @Bean
    public LazyBeanC lazyBeanC(LazyBeanD lazyBeanD) {
        return new LazyBeanC(lazyBeanD);
    }

    @Lazy
    @Bean
    public LazyBeanD lazyBeanD(LazyBeanC lazyBeanC) {
        return new LazyBeanD(lazyBeanC);
    }

    private class LazyBeanA {
        @Autowired
        private LazyBeanB lazyBeanB;

        public void sayHello() {
            System.out.println("LazyBeanA Hello World!");
        }
    }

    public class LazyBeanC {
        private LazyBeanD lazyBeanD;

        public void sayHello() {
            System.out.println("LazyBeanC Hello World!");
        }

        public LazyBeanC(LazyBeanD lazyBeanD) {
            this.lazyBeanD = lazyBeanD;
        }
    }


    public class LazyBeanD {
        private LazyBeanC lazyBeanC;

        public void sayHello() {
            System.out.println("LazyBeanD Hello World!");
        }

        public LazyBeanD(LazyBeanC lazyBeanC) {
            this.lazyBeanC = lazyBeanC;
        }
    }

    public class LazyBeanB {

        @Autowired
        private LazyBeanA lazyBeanA;

        public void sayHello() {
            System.out.println("LazyBeanB Hello World!");
        }
    }
}