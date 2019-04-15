package com.hjzgg.example.springboot.cfgcenter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author hujunzheng
 * @create 2018-08-12 11:00
 * 标识该field来自配置中心
 **/
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface ConfigField {
}
