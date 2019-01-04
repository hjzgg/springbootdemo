package com.hjzgg.example.springboot.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author hujunzheng
 * @create 2018-12-24 9:56
 **/
@RestController
@RequestMapping("async")
public class AsyncController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncController.class);

    /**
     * 同步请求测试
     */
    @GetMapping(value = "test1", produces = MediaType.ALL_VALUE)
    public String test1() {
        return func();
    }


    /**
     * 异步请求测试
     */
    @GetMapping(value = "test2", produces = MediaType.ALL_VALUE)
    public Callable<String> test2() {
        return this::func;
    }

    private String func() {
        LOGGER.info("开始执行业务逻辑...");
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 5)
                .forEach(value -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb.append(value).append(StringUtils.EMPTY);
                });
        LOGGER.info("结束执行业务逻辑...");
        return sb.toString();
    }
}