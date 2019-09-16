package com.hjzgg.example.springboot.configuration;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        exclude = RedissonAutoConfiguration.class
)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class TestBeanApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestBeanApplication.class, args);
    }

}

