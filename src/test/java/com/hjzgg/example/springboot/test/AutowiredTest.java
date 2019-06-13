package com.hjzgg.example.springboot.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hujunzheng
 * @create 2019-06-13 17:28
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AutowiredTest.MyConfig.class)
public class AutowiredTest {

    @Autowired(required = false)
    private TestBean testBean = new TestBean();

    @Test
    public void test() {
        System.out.println(testBean);
    }

    @Configuration
    public static class MyConfig{

    }

    public static class TestBean {

    }
}