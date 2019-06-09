package com.hjzgg.example.springboot.test;

import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author hujunzheng
 * @create 2019-01-23 15:44
 **/
public class MethodParameterTest {
    public static void main(String[] args) {
        MethodParameter mp1 = new MethodParameter(ReflectionUtils.findMethod(MethodParameterTest.class, "test", List.class), 0, 1);
        System.out.println(mp1.getGenericParameterType());//java.util.List<? extends java.lang.Comparable<? super T>>
        System.out.println(mp1.getParameterType());//interface java.util.List
        System.out.println(mp1.getNestingLevel());//1
        System.out.println(mp1.getNestedGenericParameterType());//java.util.List<? extends java.lang.Comparable<? super T>>
        System.out.println(mp1.getNestedParameterType());//interface java.util.List

        mp1.increaseNestingLevel();
        System.out.println(mp1.getGenericParameterType());//java.util.List<? extends java.lang.Comparable<? super T>>
        System.out.println(mp1.getParameterType());//interface java.util.List
        System.out.println(mp1.getNestingLevel());//2
        System.out.println(mp1.getNestedGenericParameterType());//? extends java.lang.Comparable<? super T>
        System.out.println(mp1.getNestedParameterType());//class java.lang.Object


        MethodParameter mp2 = new MethodParameter(ReflectionUtils.findMethod(MethodParameterTest.class, "test2", Map.class), 0, 1);
        System.out.println(mp2.getGenericParameterType());//java.util.Map<java.lang.String, java.util.List<java.util.List<java.lang.String>>>
        System.out.println(mp2.getParameterType());//interface java.util.Map
        System.out.println(mp2.getNestingLevel());//1
        System.out.println(mp2.getNestedGenericParameterType());//java.util.Map<java.lang.String, java.util.List<java.util.List<java.lang.String>>>
        System.out.println(mp2.getNestedParameterType());//interface java.util.Map

        mp2.increaseNestingLevel();
        System.out.println(mp2.getGenericParameterType());//java.util.Map<java.lang.String, java.util.List<java.util.List<java.lang.String>>>
        System.out.println(mp2.getParameterType());//interface java.util.Map
        System.out.println(mp2.getNestingLevel());//2
        System.out.println(mp2.getNestedGenericParameterType());//java.util.List<java.util.List<java.lang.String>>
        System.out.println(mp2.getNestedParameterType());//interface java.util.List

        System.out.println(mp2.getTypeIndexForCurrentLevel());//null
        mp2.setTypeIndexForCurrentLevel(0);
        System.out.println(mp2.getNestedGenericParameterType());//class java.lang.String
        mp2.setTypeIndexForCurrentLevel(1);
        System.out.println(mp2.getNestedGenericParameterType());//java.util.List<java.util.List<java.util.Map<java.lang.String, java.lang.String>>>
    }

    public <T> int test(List<? extends Comparable<? super T>> list) {
        return 0;
    }

    public void test2(Map<String, List<List<Map<String, String>>>> map) {
    }
}