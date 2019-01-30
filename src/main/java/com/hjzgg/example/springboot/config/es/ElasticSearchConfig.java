package com.hjzgg.example.springboot.config.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

//@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.clusternodes}")
    private String clusterNodes;

    @Value("${elasticsearch.clustername}")
    private String clusterName;

    @Bean
    public Client client() throws UnknownHostException {
        Settings esSettings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(esSettings);
        for (String nodes : clusterNodes.split(",")) {
            String InetSocket[] = nodes.split(":");
            String Address = InetSocket[0];
            Integer port = Integer.valueOf(InetSocket[1]);
            preBuiltTransportClient.addTransportAddress(new
                    InetSocketTransportAddress(InetAddress.getByName(Address), port));
        }
        Client client = preBuiltTransportClient;
        return client;
    }
}