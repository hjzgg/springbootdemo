package com.hjzgg.example.springboot.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * Spring切面获取真实代理对象工具类
 * <p>可从Spring AOP代理对象中获取被代理的真实对象实例
 *
 */
public abstract class AOPHelper {


    /**
     * 获取 目标对象
     * @param proxy 代理对象
     * @return 如果没有获取到代理对象, 返回原来的对象
     * @throws Exception
     */
    public static Object getTarget(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            return proxy;//不是代理对象
        }
        Object result = proxy;
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            while (AopUtils.isJdkDynamicProxy(result)) {
                result = getJdkDynamicProxyTargetObject(result);
            }
            if (AopUtils.isCglibProxy(result)) {
                result = getCglibProxyTargetObject(result);
            }
        } else { //cglib
            result = getCglibProxyTargetObject(proxy);
        }
        return result==null ? proxy : result;
    }


    /**
     * 从CGLib代理中获取目标对象
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

        return target;
    }


    /**
     * 从Jdk动态代理中获取目标对象
     * @param proxy 代理对象
     * @return 目标对象
     * @throws Exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        return ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
    }

}