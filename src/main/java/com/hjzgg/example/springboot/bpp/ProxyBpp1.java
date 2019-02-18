package com.hjzgg.example.springboot.bpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author hujunzheng
 * @create 2019-02-17 21:58
 **/
public class ProxyBpp1 implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyBpp1.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BppConfig.BppTestBean) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(bean.getClass());
            //标识Spring-generated proxies
            enhancer.setInterfaces(new Class[]{SpringProxy.class});
            //设置增强
            enhancer.setCallback((MethodInterceptor) (target, method, args, methodProxy) -> {
                if ("test1".equals(method.getName())) {
                    LOGGER.info("ProxyBpp1 开始执行...");
                    Object result = methodProxy.invokeSuper(target, args);
                    LOGGER.info("ProxyBpp1 结束执行...");
                    return result;
                }
                return method.invoke(target, args);
            });

            return enhancer.create();
        }
        return bean;
    }
}