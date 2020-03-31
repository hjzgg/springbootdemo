package com.hjzgg.example.springboot.utils.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.ProxySelector;
import java.security.*;

public class HttpClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

    public HttpClientFactory() {
    }

    public static HttpClient createHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().useSSL().build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            return HttpClientBuilder.create().setRoutePlanner(routePlanner).setSSLSocketFactory(sf).build();
        } catch (KeyManagementException var3) {
            logger.error("", var3);
        } catch (NoSuchAlgorithmException var4) {
            logger.error("", var4);
        }

        return null;
    }

    public static HttpClient createHttpClient(int maxTotal, int maxPerRoute) {
        try {
            SSLContext sslContext = SSLContexts.custom().useSSL().build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
            poolingHttpClientConnectionManager.setMaxTotal(maxTotal);
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
            SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            return HttpClientBuilder.create()
                    .setConnectionManager(poolingHttpClientConnectionManager)
                    .setRoutePlanner(routePlanner)
                    .setSSLSocketFactory(sf)
                    .build();
        } catch (KeyManagementException var6) {
            logger.error("", var6);
        } catch (NoSuchAlgorithmException var7) {
            logger.error("", var7);
        }

        return null;
    }

    public static HttpClient createKeyMaterialHttpClient(KeyStore keystore, String keyPassword, String[] supportedProtocols) {
        try {
            SSLContext sslContext = SSLContexts.custom().useSSL().loadKeyMaterial(keystore, keyPassword.toCharArray()).build();
            SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslContext, supportedProtocols, (String[])null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
            return HttpClientBuilder.create().setRoutePlanner(routePlanner).setSSLSocketFactory(sf).build();
        } catch (KeyManagementException var6) {
            logger.error("", var6);
        } catch (NoSuchAlgorithmException var7) {
            logger.error("", var7);
        } catch (UnrecoverableKeyException var8) {
            logger.error("", var8);
        } catch (KeyStoreException var9) {
            logger.error("", var9);
        }

        return null;
    }
}
