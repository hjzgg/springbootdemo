package com.hjzgg.example.springboot.listener;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        exclude = {RedissonAutoConfiguration.class, DataSourceAutoConfiguration.class},
        scanBasePackageClasses = ListenerApplication.class
)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ListenerApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ListenerApplication.class);
        builder.listeners(new MyApplicationListener2());
        builder.build().run(args);
    }

}

