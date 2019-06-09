package com.hjzgg.example.springboot.test;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hujunzheng
 * @create 2019-01-29 18:16
 **/
public class TypeDescriptorTest {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        TypeDescriptor td = TypeDescriptor.forObject(map);
        System.out.println(td.getMapKeyTypeDescriptor());//null
        System.out.println(td.getType());//class java.util.HashMap
        System.out.println(td.getName());//java.util.HashMap
        System.out.println(td.getObjectType());//class java.util.HashMap
        System.out.println(td.getSource());//class java.util.HashMap
        System.out.println(td.getElementTypeDescriptor());//null

        System.out.println();

        HttpHeaders headers = new HttpHeaders();
        td = TypeDescriptor.forObject(headers);
        System.out.println(td.getMapKeyTypeDescriptor().getType());//java.lang.String
        System.out.println(td.getMapValueTypeDescriptor().getType());//interface java.util.List
        System.out.println(td.getMapValueTypeDescriptor().getSource());//java.util.List<V>
        System.out.println(td.getMapValueTypeDescriptor().getObjectType());//interface java.util.List
        System.out.println(td.getType());//class org.springframework.http.HttpHeaders
        System.out.println(td.getName());//org.springframework.http.HttpHeaders
        System.out.println(td.getObjectType());//class org.springframework.http.HttpHeaders
        System.out.println(td.getSource());//class org.springframework.http.HttpHeaders
        System.out.println(td.getElementTypeDescriptor());//null
    }
}