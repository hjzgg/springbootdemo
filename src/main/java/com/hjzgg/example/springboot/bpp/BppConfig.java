package com.hjzgg.example.springboot.bpp;

import com.hjzgg.example.springboot.bpp.advisor.AnnotationClassAndMethodPointcutAdvisor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:56
 **/
@Configuration
public class BppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BppConfig.class);

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

//    @Bean
//    public BeanPostProcessor proxyBpp5(@Autowired ApplicationContext ac) {
//        return new ProxyBpp5(ac.getAutowireCapableBeanFactory());
//    }

//    @Bean
//    public PointcutAdvisor annotationClassOrMethodPointcutAdvisor() {
//        Advice advice = (MethodInterceptor) invocation -> {
//            LOGGER.info("AnnotationClassOrMethodPointcutAdvisor 开始执行...");
//            Object result = invocation.proceed();
//            LOGGER.info("AnnotationClassOrMethodPointcutAdvisor 结束执行...");
//            return result;
//        };
//        AnnotationClassOrMethodPointcutAdvisor pointcutAdvisor = new AnnotationClassOrMethodPointcutAdvisor(Arrays.asList(TestMethod.class) ,advice);
//        return pointcutAdvisor;
//    }

    @Bean
    public PointcutAdvisor annotationClassAndMethodPointcutAdvisor() {
        Advice advice = (MethodInterceptor) invocation -> {
            LOGGER.info("AnnotationClassAndMethodPointcutAdvisor 开始执行..." + invocation.getThis());
            Object result = invocation.proceed();
            LOGGER.info("AnnotationClassAndMethodPointcutAdvisor 结束执行..." + invocation.getThis());
            return result;
        };
        AnnotationClassAndMethodPointcutAdvisor pointcutAdvisor = new AnnotationClassAndMethodPointcutAdvisor(Collections.emptyList(), Arrays.asList(TestMethod.class), advice);
        return pointcutAdvisor;
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