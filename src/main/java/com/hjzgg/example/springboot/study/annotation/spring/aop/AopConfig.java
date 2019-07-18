package com.hjzgg.example.springboot.study.annotation.spring.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author hujunzheng
 * @create 2019-07-19 0:09
 **/
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class AopConfig {
}