package com.hjzgg.example.springboot.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

/**
 * @author hujunzheng
 * @create 2019-06-09 15:58
 **/

@RestController
@RequestMapping("/ms")
@Validated
public class MessageSourceController implements MessageSourceAware {
    private MessageSourceAccessor messageSourceAccessor;

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String test(HttpServletRequest request) {
        String body = String.format(
                "Local=%s, content=%s"
                , RequestContextUtils.getLocale(request).getLanguage()
                , messageSourceAccessor.getMessage("ms.test")
        );
        return body;
    }

    @GetMapping(value = "/validation")
    public String validation(@NotEmpty String test) {
        return test;
    }

    @GetMapping(value = "/validation3")
    public String validation3(@NotEmpty String test) {
        return test;
    }

    @GetMapping(value = "/validation2")
    public String validation2(@NotEmpty(message = "{self.not.empty}") String test) {
        return test;
    }


    @Autowired
    private MessageSource staticMessageSource;

    /**
     * 自定义messge source
     */
    @GetMapping(value = "/testself", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String testself(HttpServletRequest request) {
        System.out.println();
        String body = String.format(
                "Local=%s, content=%s"
                , RequestContextUtils.getLocale(request).getLanguage()
                , new MessageSourceAccessor(staticMessageSource).getMessage("self.ms.test")
        );
        return body;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    @GetMapping(value = "/testformat", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public String testformat(HttpServletRequest request) {
        String body = String.format(
                "Local=%s, content=%s"
                , RequestContextUtils.getLocale(request).getLanguage()
                , messageSourceAccessor.getMessage("ms.format.test", new Object[]{"world"})
        );
        return body;
    }
}