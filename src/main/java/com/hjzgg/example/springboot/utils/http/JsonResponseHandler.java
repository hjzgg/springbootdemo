package com.hjzgg.example.springboot.utils.http;


import com.hjzgg.example.springboot.utils.JacksonHelper;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonResponseHandler {

    private static Map<String, ResponseHandler<?>> map = new ConcurrentHashMap<>();

    public JsonResponseHandler() {
    }

    public static <T> ResponseHandler<T> createResponseHandler(final Class<T> clazz) {
        if (map.containsKey(clazz.getName())) {
            return (ResponseHandler) map.get(clazz.getName());
        } else {
            ResponseHandler<T> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    String str = EntityUtils.toString(entity);
                    return JacksonHelper.getObjectMapper().readValue(str, clazz);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            map.put(clazz.getName(), responseHandler);
            return responseHandler;
        }
    }
}
