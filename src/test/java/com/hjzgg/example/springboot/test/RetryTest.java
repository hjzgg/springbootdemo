package com.hjzgg.example.springboot.test;

import com.hjzgg.example.springboot.utils.Retry;

/**
 * @author hujunzheng
 * @create 2019-03-22 1:46
 **/
public class RetryTest {
    public static void main(String[] args) {
        Retry retry = new Retry(6, 20 * 1000);
        retry.execute(ctx -> {
            System.out.println("hehe");
            throw new RuntimeException();
        });
    }
}