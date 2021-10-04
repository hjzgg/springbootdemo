package com.hjzgg.example.springboot.test;

import com.hjzgg.example.springboot.i18n.MessageSourceConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;

/**
 * @author hujunzheng
 * @create 2019-06-09 16:08
 **/
@ContextConfiguration(classes = {
        MessageSourceTest.class
        , MessageSourceConfig.class
        , MessageSourceAutoConfiguration.class
        , ValidationAutoConfiguration.class
})
public class MessageSourceTest extends SpringbootWarApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMessageSource() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/test")
//                        .header("Accept-Language", "zh")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testValidation() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/validation")
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testValidation2() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/validation2")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testValidation3() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/validation3")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testFormatMessageSource() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/testformat")
//                        .header("Accept-Language", "zh")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testSelfMessageSource() throws Exception {
        String content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/testself")
//                        .header("Accept-Language", "zh")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(content);
    }

}