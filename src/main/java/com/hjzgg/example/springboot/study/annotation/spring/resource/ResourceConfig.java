package com.hjzgg.example.springboot.study.annotation.spring.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author hujunzheng
 * @create 2019-07-18 22:52
 **/
@Configuration
public class ResourceConfig {

    private ResourceBean resourceBean;

    private ResourceBean rb;

    private ParentBean childBean;

    @Resource
    public void setChildBean(ParentBean childBean) {
        this.childBean = childBean;
        System.out.println("childBean 默认装配：" + this.childBean);
    }

    @Resource
    public void setResourceBean(ResourceBean resourceBean) {
        this.resourceBean = resourceBean;
        System.out.println("resourceBean 默认装配：" + this.resourceBean);
    }

    @Resource(name = "resourceBean1")
    public void setRb(ResourceBean rb) {
        this.rb = rb;
        System.out.println("rb 默认装配：" + this.rb);
    }

    @Bean
    public ResourceBean resourceBean() {
        return new ResourceBean();
    }

    @Bean("resourceBean1")
    public ResourceBean resourceBeanx() {
        return new ResourceBean();
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
    public static class ChildBean extends ParentBean {
    }
}