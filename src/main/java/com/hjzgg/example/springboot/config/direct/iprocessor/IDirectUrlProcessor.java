package com.hjzgg.example.springboot.config.direct.iprocessor;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface IDirectUrlProcessor {
    /**
     * 接口直达策略方法
     * 处理接口直达请求
     * */
    ResponseEntity<String> handle(HttpServletRequest request) throws Exception;

    /**
     * 处理器是否支持当前直达请求
     * */
    boolean support(HttpServletRequest request);
}