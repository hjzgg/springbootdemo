package com.hjzgg.example.springboot.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.*;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

/**
 * 目前这种方案会有如下异常信息，需要优化（https://blog.csdn.net/ado1986/article/details/48268507）
 * The target server failed to respond（目标服务器返回失败）
 * <p>
 * 优化方案：https://howtodoinjava.com/spring-restful/resttemplate-httpclient-java-config/
 */
@Configuration
public class ApigatewayDirectConfig implements InitializingBean {
    private static Logger LOGGER = LoggerFactory.getLogger(ApigatewayDirectConfig.class);
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

    private HandlerMethod handlerMethod;

    @Autowired
    private RestTemplate directRestTemplate;

    @Bean
    public SimpleUrlHandlerMapping directUrlHandlerMapping(@Autowired RequestMappingHandlerAdapter handlerAdapter) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Controller> urlMappings = Maps.newHashMap();
        urlMappings.put("/alipay-applet/direct/**", new AbstractController() {
            @Override
            protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
                Assert.notNull(handlerAdapter, "直达路由Handler不能为NULL...");
                String accept = request.getHeader("Accept");
                request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Sets.newHashSet(MediaType.APPLICATION_JSON_UTF8));
                if (StringUtils.isNotBlank(accept) && !accept.contains(MediaType.ALL_VALUE)) {
                    request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Sets.newHashSet(
                            Arrays.stream(accept.split(","))
                                    .map(value -> MediaType.parseMediaType(value.trim()))
                                    .toArray(size -> new MediaType[size])
                    ));
                }
                return handlerAdapter.handle(request, response, handlerMethod);
            }
        });
        mapping.setUrlMap(urlMappings);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return mapping;
    }

    @Override
    public void afterPropertiesSet() {
        DirectUrlProcessor directUrlProcessor = new DirectUrlProcessor(this.directRestTemplate);
        this.handlerMethod = new HandlerMethod(directUrlProcessor, ReflectionUtils.findMethod(DirectUrlProcessor.class, "handle", HttpServletRequest.class));
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

                protected Charset getCharset(ClientHttpResponse response) {
                    HttpHeaders headers = response.getHeaders();
                    MediaType contentType = headers.getContentType();
                    return contentType != null ? contentType.getCharset() : null;
                }
            });
            // 添加内容转换器
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            return restTemplate;
        } catch (Exception e) {
            throw new Exception("网络异常或请求错误.", e);
        }
    }

    private static class DirectUrlProcessor {
        private RestTemplate restTemplate;

        public DirectUrlProcessor(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public ResponseEntity<String> handle(HttpServletRequest request) throws Exception {
            HttpMethod method = HttpMethod.resolve(request.getMethod());
            Object body;
            String query = StringUtils.isBlank(request.getQueryString()) ? StringUtils.EMPTY : ("?" + request.getQueryString());
            URI uri = URI.create("http://localhost:8080/apigateway" + request.getServletPath().replace("/alipay-applet/direct", StringUtils.EMPTY) + query);
            if (method == HttpMethod.GET) {
                body = null;
            } else {
                body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                        .lines()
                        .collect(Collectors.joining());
                // post/form
                if (StringUtils.isBlank((String) body)) {
                    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                    if (!CollectionUtils.isEmpty(request.getParameterMap())) {
                        request.getParameterMap()
                                .forEach(
                                        (paramName, paramValues) -> Arrays.stream(paramValues)
                                                .forEach(paramValue -> params.add(paramName, paramValue))
                                );
                        body = params;
                    }
                    uri = URI.create("http://localhost:8080/apigateway" + request.getServletPath().replace("/alipay-applet/direct", StringUtils.EMPTY));
                }
            }

            HttpHeaders headers = new HttpHeaders();
            CollectionUtils.toIterator(request.getHeaderNames())
                    .forEachRemaining(headerName -> CollectionUtils.toIterator(request.getHeaders(headerName))
                            .forEachRemaining(headerValue -> headers.add(headerName, headerValue)));

            RequestEntity directRequest = new RequestEntity(body, headers, method, uri);
            try {
                LOGGER.info("接口直达RequestEntity = " + directRequest);
                ResponseEntity<String> directResponse = this.restTemplate.exchange(directRequest, String.class);
                LOGGER.info(String.format("接口直达URL = %s, ResponseEntity = %s", directRequest.getUrl(), directResponse));
                return ResponseEntity.ok(directResponse.getBody());
            } catch (RestClientResponseException e) {
                LOGGER.error("restapi  TRRRRRRRRRR T YUV     VGFCV  VCGF               C VC  X内部异常", e);
                return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
            } catch (Exception e) {
                LOGGER.error("restapi 内部异常，未知错误...", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("restapi 内部异常，未知错误...");
            }
        }
    }
}
