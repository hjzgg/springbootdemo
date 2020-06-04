package com.hjzgg.example.springboot.config.es.routing;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document {
    //ES客户端名称
    String clientName();

    //mapping信息
    String mappingPath() default "";

    //索引名称
    String indexName();

    //类型名称
    String type() default "";

    //分片
    short shards() default 5;

    //备份
    short replicas() default 1;

    //刷新时间
    String refreshInterval() default "1s";

    String indexStoreType() default "fs";

    boolean useServerConfig() default false;
}