package com.hjzgg.example.springboot.study.annotation.spring.bean.lookup;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

/**
 * @author hujunzheng
 * @create 2019-07-15 23:19
 **/
@Component
public abstract class BeanHandler {

    @Lookup
    public abstract LookupBean getLookupBean();
}