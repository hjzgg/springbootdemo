package com.hjzgg.example.springboot.bpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:58
 **/
public class ProxyBpp3 implements InstantiationAwareBeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyBpp3.class);

    private final AutowireCapableBeanFactory autowireBeanFactory;

    ProxyBpp3(AutowireCapableBeanFactory autowireBeanFactory) {
        this.autowireBeanFactory = autowireBeanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass.equals(BppConfig.BppTestBean.class)) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanClass);
            //标识Spring-generated proxies
            enhancer.setInterfaces(new Class[]{SpringProxy.class});
            //设置增强
            enhancer.setCallback((MethodInterceptor) (target, method, args, methodProxy) -> {
                if ("test1".equals(method.getName())) {
                    LOGGER.info("ProxyBpp3 开始执行...");
                    Object result = methodProxy.invokeSuper(target, args);
                    LOGGER.info("ProxyBpp3 结束执行...");
                    return result;
                }
                return methodProxy.invokeSuper(target, args);
            });

            return this.postProcess(enhancer.create());
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