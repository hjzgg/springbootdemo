package com.hjzgg.example.springboot.test;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hujunzheng
 * @create 2019-03-21 14:06
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissonTest {

    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Test
    public void test() throws InterruptedException {
        Map<String, Map<String, String>> config = Maps.newHashMap();
        Map<String, String> childConfig = Maps.newHashMap();
        childConfig.put("name", "hjzgg");
        config.put("child1", childConfig);
        RBucket<Map<String, Map<String, String>>> bucket = redissonClient.getBucket("bucket1");
        bucket.set(config);

        TimeUnit.SECONDS.sleep(2);

        System.out.println(bucket.compareAndSet(config, config));

        childConfig.put("name", "qyx");
        System.out.println(bucket.compareAndSet(config, config));

        System.out.println(bucket.get());
    }
}