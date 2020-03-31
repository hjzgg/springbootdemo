package com.hjzgg.example.springboot.test;

import com.hjzgg.example.springboot.bpp.BppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hujunzheng
 * @create 2019-02-17 22:27
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BppConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class BppTest {

    @Autowired
    private BppConfig.BppTestBean bppTestBean;

    @Test
    public void test() {
        bppTestBean.test1();
        bppTestBean.test2();
        bppTestBean.test3();
    }
}