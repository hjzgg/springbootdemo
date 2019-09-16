package com.hjzgg.example.springboot.hystrix;

import com.hjzgg.example.springboot.config.hystrix.HystrixConfig;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        exclude = {RedissonAutoConfiguration.class, DataSourceAutoConfiguration.class},
        scanBasePackageClasses = HystrixConfig.class
)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class HystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixApplication.class, args);
    }

}

