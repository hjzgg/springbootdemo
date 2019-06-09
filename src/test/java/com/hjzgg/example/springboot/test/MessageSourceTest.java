package com.hjzgg.example.springboot.test;

import com.hjzgg.example.springboot.i18n.MessageSourceController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author hujunzheng
 * @create 2019-06-09 16:08
 **/
@ContextConfiguration(classes = {
        MessageSourceTest.class
        , MessageSourceController.class
        , MessageSourceAutoConfiguration.class
})
public class MessageSourceTest extends SpringbootApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testMessageSource() throws Exception {
        byte[] content = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/ms/test")
//                        .header("Accept-Language", "zh")
                                .locale(Locale.CHINA)
                )
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        System.out.println(new String(content, StandardCharsets.UTF_8));
    }
}