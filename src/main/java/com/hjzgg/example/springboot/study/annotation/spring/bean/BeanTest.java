package com.hjzgg.example.springboot.study.annotation.spring.bean;

import com.hjzgg.example.springboot.study.annotation.spring.bean.lazy.LazyBean;
import com.hjzgg.example.springboot.study.annotation.spring.bean.lookup.BeanHandler;
import com.hjzgg.example.springboot.study.annotation.spring.bean.lookup.LookupBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author hujunzheng
 * @create 2019-07-15 22:05
 **/

public class BeanTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanConfig.class);
//        EmptyBean eb = ac.getBean(EmptyBean.class);
//        eb.sayHello();

//        InitialBean ib = ac.getBean(InitialBean.class);
//        ib.sayHello();
//        ac.close();

//        InitialBean2 ib2 = ac.getBean(InitialBean2.class);
//        ac.close();

        //lookup 解耦spring依赖
//        LookupBean lb1 = ac.getBean(BeanHandler.class).getLookupBean();
//        LookupBean lb2 = ac.getBean(BeanHandler.class).getLookupBean();
//        System.out.println(lb1.equals(lb2));
//
//        LookupBean lb3 = ac.getBean(LookupBean.class);
//        LookupBean lb4 = ac.getBean(LookupBean.class);
//        System.out.println(lb3.equals(lb4));

        LazyBean lb = ac.getBean(LazyBean.class);
        lb.sayHello();

    }
}