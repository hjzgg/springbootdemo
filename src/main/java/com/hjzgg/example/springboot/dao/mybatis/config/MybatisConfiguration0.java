package com.hjzgg.example.springboot.dao.mybatis.config;

import com.hjzgg.example.springboot.dao.mybatis.intercepts.SqlCostInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hujunzheng
 * @create 2019-04-15 10:21
 **/
@Configuration
@MapperScan("com.hjzgg.example.springboot.dao.mybatis")
public class MybatisConfiguration0 {

    /**
     * 自定义mybatis 配置
     * */
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.setDefaultFetchSize(100);
            configuration.setDefaultStatementTimeout(50);
            //支持驼峰
            configuration.setMapUnderscoreToCamelCase(true);
            //sql打印和执行时间拦截器
            configuration.addInterceptor(new SqlCostInterceptor());
        };
    }
}