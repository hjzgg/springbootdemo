package com.hjzgg.example.springboot.study.annotation.spring.bean;

import com.hjzgg.example.springboot.study.annotation.spring.bean.EmptyBean;
import com.hjzgg.example.springboot.study.annotation.spring.bean.InitialBean;
import com.hjzgg.example.springboot.study.annotation.spring.bean.InitialBean2;
import com.hjzgg.example.springboot.study.annotation.spring.bean.lazy.LazyBean;
import com.hjzgg.example.springboot.study.annotation.spring.bean.lookup.LookupBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:06
 **/
@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class BeanConfig {
    @Bean
    public EmptyBean emptyBean() {
        return new EmptyBean();
    }

//    @Bean
    public InitialBean initialBean() {
        return new InitialBean();
    }

//    @Bean(initMethod = "init")
    public InitialBean2 initialBean2() {
        return new InitialBean2();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public LookupBean lookupBean() {
        return new LookupBean();
    }

    @Lazy
    @Bean
    public LazyBean lazyBean() {
        return new LazyBean();
    }
}