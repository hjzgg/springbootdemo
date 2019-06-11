package com.hjzgg.example.springboot;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(
        exclude = RedissonAutoConfiguration.class
        //, scanBasePackages = "com.hjzgg.example.springboot.configuration"//測試bean覆蓋
)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableRetry
public class SpringbootApplication {

    public static void main(String[] args) {
        System.setProperty("xxx.system.id", "test_system");
        System.setProperty("xxx.app.id", "test_app");
        System.setProperty("groupenv", "x");
        SpringApplication.run(SpringbootApplication.class, args);
    }

}

