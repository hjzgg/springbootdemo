package com.hjzgg.example.springboot.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * @author hujunzheng
 * @create 2019-10-07 16:06
 **/
@Component
public class MyApplicationListener1 implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("MyApplicationListener1 ==> " + ClassUtils.getShortName(event.getClass()));
    }
}