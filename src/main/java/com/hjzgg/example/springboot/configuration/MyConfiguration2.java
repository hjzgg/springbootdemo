package com.hjzgg.example.springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration2 {

    @Bean
    public TestBean testBean() {
        return new TestBean();
    }
}