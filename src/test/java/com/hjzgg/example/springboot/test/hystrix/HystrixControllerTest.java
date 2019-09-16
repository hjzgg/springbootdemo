package com.hjzgg.example.springboot.test.hystrix;

import com.hjzgg.example.springboot.test.SpringbootApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.CountDownLatch;

/**
 * @author hujunzheng
 * @create 2019-09-16 17:51
 **/
@ContextConfiguration(classes = {
        HystrixControllerTest.class
})
@ComponentScan("com.hjzgg.example.springboot.hystrix")
public class HystrixControllerTest extends SpringbootApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHystrixController() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            String content = null;
            try {
                content = mockMvc
                        .perform(
                                MockMvcRequestBuilders.get("/hystrix/test")
                                        .param("groupKey", "groupKey1")
                                        .param("commandKey", "commandKey1")
                                        .param("poolKey", "poolKey1")
                                        .param("index", String.valueOf(finalI))
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(content);
        }

        for (int i = 10; i < 20; i++) {
            int finalI = i;
            new Thread(() -> {
                String content = null;
                try {
                    cdl.await();
                    content = mockMvc
                            .perform(
                                    MockMvcRequestBuilders.get("/hystrix/test")
                                            .param("groupKey", "groupKey2")
                                            .param("commandKey", "commandKey2")
                                            .param("poolKey", "poolKey2")
                                            .param("index", String.valueOf(finalI))
                            )
                            .andReturn()
                            .getResponse()
                            .getContentAsString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(content);
            }).start();
        }

        cdl.countDown();

        System.in.read();
    }
}