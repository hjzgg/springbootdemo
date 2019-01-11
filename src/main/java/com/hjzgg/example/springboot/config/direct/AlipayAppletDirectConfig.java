package com.hjzgg.example.springboot.config.direct;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hjzgg.example.springboot.config.direct.iprocessor.IDirectUrlProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

@Configuration
public class AlipayAppletDirectConfig {
    /**
     * 最大连接数
     */
    private static final int MAX_CONNECTION_TOTAL = 300;
    /**
     * 路由并发数
     */
    private static final int ROUTE_MAX_COUNT = 200;
    /**
     * 重试次数
     */
    private static final int RETRY_COUNT = 3;
    /**
     * 连接超时
     */
    private static final int CONNECTION_TIME_OUT = 45000;
    /**
     * 数据超时
     */
    private static final int READ_TIME_OUT = 75000;
    /**
     * 连接等待
     */
    private static final int CONNECTION_REQUEST_TIME_OUT = 5000;

    /**
     * 连接空闲超时
     */
    private static final int CONNECTION_IDLE_TIME_OUT = 5000;

    @Bean
    public SimpleUrlHandlerMapping directUrlHandlerMapping(@Autowired RequestMappingHandlerAdapter handlerAdapter
            , ObjectProvider<List<IDirectUrlProcessor>> directUrlProcessorsProvider) {
        List<IDirectUrlProcessor> directUrlProcessors = directUrlProcessorsProvider.getIfAvailable();
        Assert.notEmpty(directUrlProcessors, "接口直达解析器（IDirectUrlProcessor）列表不能为空！！！");
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Controller> urlMappings = Maps.newHashMap();
        urlMappings.put("/alipay-applet/direct/**", new AbstractController() {
            @Override
            protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
                for (IDirectUrlProcessor directUrlProcessor : directUrlProcessors) {
                    if (directUrlProcessor.support(request)) {
                        String accept = request.getHeader("Accept");
                        request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Sets.newHashSet(MediaType.APPLICATION_JSON_UTF8));
                        if (StringUtils.isNotBlank(accept) && !accept.contains(MediaType.ALL_VALUE)) {
                            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Sets.newHashSet(
                                    Arrays.stream(accept.split(","))
                                            .map(value -> MediaType.parseMediaType(value.trim()))
                                            .toArray(size -> new MediaType[size])
                            ));
                        }
                        HandlerMethod handlerMethod = new HandlerMethod(directUrlProcessor, ReflectionUtils.findMethod(IDirectUrlProcessor.class, "handle", HttpServletRequest.class));
                        return handlerAdapter.handle(request, response, handlerMethod);
                    }
                }
                throw new RuntimeException("未找到具体的接口直达处理器...");
            }
        });
        mapping.setUrlMap(urlMappings);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return mapping;
    }

    @Bean
    public RestTemplate directRestTemplate() throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                @Override
                public void handleError(ClientHttpResponse response) throws IOException {
                    throw new RestClientResponseException(response.getStatusCode().value() + " " + response.getStatusText(),
                            response.getStatusCode().value()
                            , response.getStatusText()
                            , response.getHeaders()
                            , getResponseBody(response)
                            , getCharset(response));
                }

                @Override
                protected byte[] getResponseBody(ClientHttpResponse response) {
                    try {
                        InputStream responseBody = response.getBody();
                        if (responseBody != null) {
                            return FileCopyUtils.copyToByteArray(responseBody);
                        }
                    } catch (IOException ex) {
                        // ignore
                    }
                    return new byte[0];
                }

                @Override
                protected Charset getCharset(ClientHttpResponse response) {
                    HttpHeaders headers = response.getHeaders();
                    MediaType contentType = headers.getContentType();
                    return contentType != null ? contentType.getCharset() : null;
                }
            });
            // 修改StringHttpMessageConverter内容转换器
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            return restTemplate;
        } catch (Exception e) {
            throw new Exception("网络异常或请求错误.", e);
        }
    }

    /**
     * 接受未信任的请求
     *
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

        httpClientBuilder.setSSLContext(sslContext)
                .setMaxConnTotal(MAX_CONNECTION_TOTAL)
                .setMaxConnPerRoute(ROUTE_MAX_COUNT)
                .evictIdleConnections(CONNECTION_IDLE_TIME_OUT, TimeUnit.MILLISECONDS);

        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(RETRY_COUNT, true));
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        CloseableHttpClient client = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        clientHttpRequestFactory.setConnectTimeout(CONNECTION_TIME_OUT);
        clientHttpRequestFactory.setReadTimeout(READ_TIME_OUT);
        clientHttpRequestFactory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT);
        clientHttpRequestFactory.setBufferRequestBody(false);
        return clientHttpRequestFactory;
    }
}
