package com.hjzgg.example.springboot;

/**
 * @author hujunzheng
 * @create 2019-05-30 15:48
 **/

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UrlPathHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockTest.class)
public class MockTest {
    @Autowired
    private WebApplicationContext context;

    protected MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void mock() {
        MockHttpServletRequest request = MockMvcRequestBuilders.get("http://localhost:8080/test/test1/test2").buildRequest(context.getServletContext());
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        System.out.println(urlPathHelper.getLookupPathForRequest(request));
        System.out.println(urlPathHelper.getContextPath(request));
        System.out.println(urlPathHelper.getServletPath(request));
        System.out.println(urlPathHelper.getRequestUri(request));
    }
}