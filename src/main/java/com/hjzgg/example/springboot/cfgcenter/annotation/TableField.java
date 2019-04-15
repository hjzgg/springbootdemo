package com.hjzgg.example.springboot.cfgcenter.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author hujunzheng
 * @create 2018-07-06 9:59
 **/
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface TableField {
    String columnName() default StringUtils.EMPTY;
}