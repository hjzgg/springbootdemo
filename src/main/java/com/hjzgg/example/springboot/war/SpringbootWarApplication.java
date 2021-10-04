package com.hjzgg.example.springboot.war;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 1、启动类继承{@link org.springframework.boot.web.servlet.support.SpringBootServletInitializer}
 * 2、修改pom文件 <packaging>jar</packaging> 为 <packaging>war</packaging>
 * 3、修改pom文件 dependencyManagement中新增spring-boot-starter-tomcat管理，scope指定为 provided
 * 4、IDEA配置tomcat启动war乱码解决：conf/logging.properties文件里 *.encoding = UTF-8 修改为 *.encoding = GBK、idea VM options添加-Dfile.encoding=GBK
 */

@SpringBootApplication(
        exclude = {RedissonAutoConfiguration.class, DataSourceAutoConfiguration.class}
)
public class SpringbootWarApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWarApplication.class, args);
    }
}

