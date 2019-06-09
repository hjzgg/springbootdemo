package com.hjzgg.example.springboot.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hujunzheng
 * @create 2019-06-09 15:58
 **/

@RestController
@RequestMapping("/ms")
public class MessageSourceController implements MessageSourceAware {
    private MessageSourceAccessor messageSourceAccessor;

    @GetMapping(value="/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(HttpServletRequest request) {
        String body = String.format(
                "Local=%s, content=%s"
                , RequestContextUtils.getLocale(request).getLanguage()
                , messageSourceAccessor.getMessage("ms.test")
        );
        return body;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }
}