package com.hjzgg.example.springboot.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;

import java.util.Locale;

/**
 * @author hujunzheng
 * @create 2019-08-26 1:24
 **/
@Configuration
@ComponentScan
public class MessageSourceConfig {
    @Bean
    public MessageSource staticMessageSource() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("self.ms.test", Locale.SIMPLIFIED_CHINESE, "测试");
        messageSource.addMessage("self.ms.test", Locale.ENGLISH, "test");
        return messageSource;
    }
}