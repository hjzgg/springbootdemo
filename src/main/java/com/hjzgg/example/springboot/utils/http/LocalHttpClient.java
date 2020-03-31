package com.hjzgg.example.springboot.utils.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(LocalHttpClient.class);
    public static Header jsonHeader;
    public static Header formHeader;
    protected static HttpClient httpClient;
    private static Map<String, HttpClient> httpClient_mchKeyStore;

    private LocalHttpClient() {
    }

    public static void init(int maxTotal, int maxPerRoute) {
        httpClient = HttpClientFactory.createHttpClient(maxTotal, maxPerRoute);
    }

    public static void initMchKeyStore(String mch_id, String keyStoreFilePath) {
        try (FileInputStream instream = new FileInputStream(new File(keyStoreFilePath))) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(instream, mch_id.toCharArray());
            HttpClient httpClient = HttpClientFactory.createKeyMaterialHttpClient(keyStore, mch_id, new String[]{"TLSv1"});
            httpClient_mchKeyStore.put(mch_id, httpClient);
        } catch (KeyStoreException var12) {
            logger.error("", var12);
        } catch (FileNotFoundException var13) {
            logger.error("", var13);
        } catch (NoSuchAlgorithmException var14) {
            logger.error("", var14);
        } catch (CertificateException var15) {
            logger.error("", var15);
        } catch (IOException var16) {
            logger.error("", var16);
        }

    }

    public static String get(String uri) throws IOException {
        HttpUriRequest request = RequestBuilder.get().setUri(uri).build();
        HttpResponse response = execute(request);
        return response != null ? EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8")) : "";
    }

    public static String jsonPost(String uri, String body) throws IOException {
        HttpUriRequest request = RequestBuilder.post().setHeader(jsonHeader).setUri(uri).setEntity(new StringEntity(body, Charset.forName("UTF-8"))).build();
        HttpResponse response = execute(request);
        return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
    }

    public static String postForm(String uri, MultiValueMap<String, String> params) throws IOException {
        params.keySet()
                .stream()
                .flatMap(name -> params.get(name)
                        .stream()
                        .map(value -> new BasicNameValuePair(name, value))
                )
                .collect(Collectors.toList());

        HttpEntity httpEntity = new UrlEncodedFormEntity(
                params.keySet()
                        .stream()
                        .flatMap(name -> params.get(name)
                                .stream()
                                .map(value -> new BasicNameValuePair(name, value))
                        )
                        .collect(Collectors.toList())
                , StandardCharsets.UTF_8
        );
        HttpUriRequest request = RequestBuilder.post()
                .setConfig(serviceConfig(7000))
                .setHeader(formHeader)
                .setUri(uri)
                .setEntity(httpEntity)
                .build();
        HttpResponse response = execute(request);
        System.out.println(response.getStatusLine());
        return EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
    }

    public static HttpResponse execute(HttpUriRequest request) {
        try {
            return httpClient.execute(request);
        } catch (ClientProtocolException var2) {
            logger.error("", var2);
        } catch (IOException var3) {
            logger.error("", var3);
        }

        return null;
    }

    public static <T> T execute(HttpUriRequest request, ResponseHandler<T> responseHandler) {
        try {
            return httpClient.execute(request, responseHandler);
        } catch (ClientProtocolException var3) {
            logger.error("", var3);
        } catch (IOException var4) {
            logger.error("", var4);
        }

        return null;
    }

    public static <T> T executeJsonResult(HttpUriRequest request, Class<T> clazz) {
        return execute(request, JsonResponseHandler.createResponseHandler(clazz));
    }

    public static <T> T executeXmlResult(HttpUriRequest request, Class<T> clazz) {
        return execute(request, XmlResponseHandler.createResponseHandler(clazz));
    }

    public static <T> T keyStoreExecuteXmlResult(String mch_id, HttpUriRequest request, Class<T> clazz) {
        try {
            return httpClient_mchKeyStore.get(mch_id).execute(request, XmlResponseHandler.createResponseHandler(clazz));
        } catch (ClientProtocolException var4) {
            logger.error("", var4);
        } catch (IOException var5) {
            logger.error("", var5);
        }

        return null;
    }

    public static String getService(String url, int timeout) throws IOException {
        if (StringUtils.isBlank(url)) {
            return "";
        } else {
            if (timeout == 0) {
                timeout = 7000;
            }

            HttpUriRequest request = RequestBuilder.get().setConfig(serviceConfig(timeout)).setUri(url).build();
            HttpResponse response = execute(request);
            return entityToString(response.getEntity());
        }
    }

    public String postService(String url, int timeout) throws IOException {
        if (StringUtils.isBlank(url)) {
            return "";
        } else {
            if (timeout == 0) {
                timeout = 7000;
            }

            HttpUriRequest request = RequestBuilder.post().setConfig(serviceConfig(timeout)).setUri(url).build();
            HttpResponse response = execute(request);
            return entityToString(response.getEntity());
        }
    }

    private static RequestConfig serviceConfig(int timeout) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .setCookieSpec("compatibility")
                .setRedirectsEnabled(false)
                .build();
    }

    private static String entityToString(HttpEntity httpEntity) throws IOException {
        return IOUtils.toString(httpEntity.getContent(), StandardCharsets.UTF_8);
    }

    static {
        jsonHeader = new BasicHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        formHeader = new BasicHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        httpClient = HttpClientFactory.createHttpClient(500, 10);
        httpClient_mchKeyStore = new HashMap();
    }
}
