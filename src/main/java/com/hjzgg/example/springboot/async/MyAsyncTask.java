package com.hjzgg.example.springboot.async;

import com.hjzgg.example.springboot.config.MyAsyncConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author hujunzheng
 * @create 2019-03-13 15:31
 **/
@Component
public class MyAsyncTask {

    private static Logger LOGGER = LoggerFactory.getLogger(MyAsyncConfigurer.class);

    /**
     * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
     * <p>
     * Spring Bean创建-解决依赖 参考链接：https://blog.csdn.net/finalcola/article/details/81537380
     */
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