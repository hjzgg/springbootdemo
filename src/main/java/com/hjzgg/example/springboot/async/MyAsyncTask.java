package com.hjzgg.example.springboot.async;

import com.hjzgg.example.springboot.config.MyAsyncConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author hujunzheng
 * @create 2019-03-13 15:31
 **/
@Component
public class MyAsyncTask {

    private static Logger LOGGER = LoggerFactory.getLogger(MyAsyncConfigurer.class);

    @Lazy
    @Autowired
    private MyInnerAsyncTask myInnerAsyncTask;

    @Async
    public void async() {
        LOGGER.error("async");
    }

    public void asyncInner() {
        myInnerAsyncTask.async();
    }

    private class MyInnerAsyncTask {
        @Async
        public void async() {
            LOGGER.error("async inner");
        }
    }

    @Configuration
    public static class MyAsyncTaskConfiguration {
        @Bean
        public MyInnerAsyncTask myInnerAsyncTask(MyAsyncTask myAsyncTask) {
            return myAsyncTask.new MyInnerAsyncTask();
        }
    }
}