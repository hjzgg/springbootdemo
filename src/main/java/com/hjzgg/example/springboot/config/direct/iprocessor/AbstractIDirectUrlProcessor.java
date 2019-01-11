package com.hjzgg.example.springboot.config.direct.iprocessor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class AbstractIDirectUrlProcessor implements IDirectUrlProcessor {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractIDirectUrlProcessor.class);

    @Autowired
    private RestTemplate directRestTemplate;

    /**
     * 接口直达模板方法
     * */
    protected ResponseEntity<String> handleRestfulCore(HttpServletRequest request, URI uri, String userId) throws Exception {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        Object body;
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
            }
        }

        HttpHeaders headers = new HttpHeaders();
        CollectionUtils.toIterator(request.getHeaderNames())
                .forEachRemaining(headerName -> CollectionUtils.toIterator(request.getHeaders(headerName))
                        .forEachRemaining(headerValue -> headers.add(headerName, headerValue)));

        RequestEntity directRequest = new RequestEntity(body, headers, method, uri);
        try {
            LOGGER.info(String.format("接口直达UserId = %s, RequestEntity = %s", userId, directRequest));
            ResponseEntity<String> directResponse = directRestTemplate.exchange(directRequest, String.class);
            LOGGER.info(String.format("接口直达UserId = %s, URL = %s, ResponseEntity = %s", userId, directRequest.getUrl(), directResponse));
            return ResponseEntity.ok(directResponse.getBody());
        } catch (RestClientResponseException e) {
            LOGGER.error("restapi 内部异常", e);
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            LOGGER.error("restapi 内部异常，未知错误...", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("restapi 内部异常，未知错误...");
        }
    }
}