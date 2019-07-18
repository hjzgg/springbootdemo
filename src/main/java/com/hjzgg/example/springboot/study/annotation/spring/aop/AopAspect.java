package com.hjzgg.example.springboot.study.annotation.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author hujunzheng
 * @create 2019-07-19 0:10
 **/
@Component
@Aspect
public class AopAspect {
    @Before(
            "execution(* com.hjzgg.example.springboot.study.annotation.spring.aop..*.before(..)) "
                    + "&& @annotation(com.hjzgg.example.springboot.utils.sign.Sign)"
    )
    public void before() {
        System.out.println("I am before");
    }

    @After(
            "execution(* com.hjzgg.example.springboot.study.annotation.spring.aop..*.after(..)) "
                    + "&& @annotation(com.hjzgg.example.springboot.utils.sign.Sign)"
    )
    public void after() {
        System.out.println("I am after");
    }

    @Around(
            value = "execution(* com.hjzgg.example.springboot.study.annotation.spring.aop..*.test(..)) "
                    + "&& @annotation(com.hjzgg.example.springboot.utils.sign.Sign) "
                    + " && args(content)"
    )
    public void around(ProceedingJoinPoint pjp, String content) throws Throwable {
        System.out.println("I am before>>>>>>>>>>" + content);
        pjp.proceed();
        System.out.println("I am after>>>>>>>>>>>" + content);
    }
}