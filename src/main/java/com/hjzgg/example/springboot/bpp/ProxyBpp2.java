package com.hjzgg.example.springboot.bpp;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:58
 **/
public class ProxyBpp2 implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyBpp2.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BppConfig.BppTestBean) {
            ProxyFactoryBean pfb = new ProxyFactoryBean();
            pfb.setTarget(bean);
            pfb.setAutodetectInterfaces(false);
            NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
            advisor.addMethodName("test1");
            advisor.setAdvice((MethodInterceptor) invocation -> {
                LOGGER.info("ProxyBpp2 开始执行...");
                Object result = invocation.getMethod().invoke(invocation.getThis(), invocation.getArguments());
                LOGGER.info("ProxyBpp2 结束执行...");
                return result;
            });
            pfb.addAdvisor(advisor);

            return pfb.getObject();
        }
        return bean;
    }
}