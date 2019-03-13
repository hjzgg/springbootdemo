package com.hjzgg.example.springboot.bpp;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:58
 **/
public class ProxyBpp4 implements InstantiationAwareBeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyBpp4.class);

    private final AutowireCapableBeanFactory autowireBeanFactory;

    ProxyBpp4(AutowireCapableBeanFactory autowireBeanFactory) {
        this.autowireBeanFactory = autowireBeanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(BppConfig.BppTestBean.class)) {
            ProxyFactoryBean pfb = new ProxyFactoryBean();
            pfb.setTarget(this.postProcess(BeanUtils.instantiateClass(beanClass)));
            pfb.setAutodetectInterfaces(false);
            NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
            advisor.addMethodName("test1");
            advisor.setAdvice((MethodInterceptor) invocation -> {
                LOGGER.info("ProxyBpp4 开始执行...");
                Object result = invocation.proceed();
                LOGGER.info("ProxyBpp4 结束执行...");
                return result;
            });
            pfb.addAdvisor(advisor);

            return pfb.getObject();
        }
        return null;
    }

    public <T> T postProcess(T object) {
        if (object == null) {
            return null;
        }
        T result;
        try {
            // 使用容器autowireBeanFactory标准依赖注入方法autowireBean()处理 object对象的依赖注入
            this.autowireBeanFactory.autowireBean(object);
            // 使用容器autowireBeanFactory标准初始化方法initializeBean()初始化对象 object
            result = (T) this.autowireBeanFactory.initializeBean(object,
                    object.toString());
        } catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException(
                    "Could not postProcess " + object + " of type " + type, e);
        }
        return result;
    }
}