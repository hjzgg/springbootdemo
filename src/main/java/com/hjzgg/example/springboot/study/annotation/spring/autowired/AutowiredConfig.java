package com.hjzgg.example.springboot.study.annotation.spring.autowired;

import com.hjzgg.example.springboot.study.annotation.spring.resource.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hujunzheng
 * @create 2019-07-18 22:32
 **/
@Configuration
public class AutowiredConfig {

    private AutowiredBean autowiredBean1;

    private AutowiredBean autowiredBean;

    private AutowiredBean ab;

    private ParentBean childBean;

    @Autowired
    public void setChildBean(ParentBean childBean) {
        this.childBean = childBean;
        System.out.println("childBean 默认装配：" + this.childBean);
    }

    @Qualifier("autowiredBean1")
    @Autowired
//    public void setAb(AutowiredBean autowiredBean) {
    public void setAb(AutowiredBean ab) {
        this.ab = ab;
        System.out.println("ab 默认装配：" + this.ab);
    }

    @Autowired
    public void setAutowiredBean(AutowiredBean autowiredBean) {
        this.autowiredBean = autowiredBean;
        System.out.println("autowiredBean 默认装配：" + this.autowiredBean);
    }

    @Autowired
    public void setAutowiredBean1(AutowiredBean autowiredBean1) {
        this.autowiredBean1 = autowiredBean1;
        System.out.println("autowiredBean1 默认装配：" + this.autowiredBean1);
    }

    @Bean
    public AutowiredBean autowiredBean() {
        return new AutowiredBean();
    }

    @Bean("autowiredBean1")
    public AutowiredBean autowiredBeanx() {
        return new AutowiredBean();
    }


    @Bean
    public ParentBean parentBean() {
        return new ParentBean();
    }

    @Bean
    public ChildBean childBean() {
        return new ChildBean();
    }

    public static class ParentBean {
    }

    public static class ChildBean extends ResourceConfig.ParentBean {
    }

}