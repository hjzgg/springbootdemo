package com.hjzgg.example.springboot.test.mybatis;

import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.CfgMapper;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.SearchVO;
import com.hjzgg.example.springboot.dao.mybatis.config.MybatisConfiguration0;
import com.hjzgg.example.springboot.test.SpringbootWarApplicationTests;
import org.junit.Test;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

/**
 * @author hujunzheng
 * @create 2020-06-04 14:07
 **/

@ContextConfiguration(classes = {
        MybatisConfiguration0.class
})
@EnableAutoConfiguration(exclude = RedissonAutoConfiguration.class)
public class MybatisTest extends SpringbootWarApplicationTests {

    @Autowired
    private CfgMapper cfgMapper;

    @Test
    public void test() throws IOException {
        SearchVO searchVO = new SearchVO();
        searchVO.setSystemId("c");
        searchVO.setAppId("b");
        searchVO.setGroupId("a");
        cfgMapper.findRecords(searchVO);
        System.in.read();
    }
}