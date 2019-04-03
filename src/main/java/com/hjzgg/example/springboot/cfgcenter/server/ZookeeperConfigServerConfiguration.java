package com.hjzgg.example.springboot.cfgcenter.server;

import com.hjzgg.example.springboot.cfgcenter.client.ZookeeperProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hujunzheng
 * @create 2019-03-31 23:55
 **/
//@Configuration
public class ZookeeperConfigServerConfiguration {

    //@Configuration
    @ConditionalOnProperty(value = "wmh.cfg.enable", matchIfMissing = true)
    public static class ZookeeperServerConfiguration {

        private static final Log log = LogFactory.getLog(ZookeeperServerConfiguration.class);

        @Autowired(required = false)
        private EnsembleProvider ensembleProvider;

        @Bean
        @ConditionalOnMissingBean
        public ZookeeperProperties zookeeperProperties() {
            return new ZookeeperProperties();
        }


        @Bean(destroyMethod = "close", name="zookeeperServerCurator")
        public CuratorFramework curatorFramework(RetryPolicy retryPolicy, ZookeeperProperties properties) throws Exception {
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
            if (this.ensembleProvider != null) {
                builder.ensembleProvider(this.ensembleProvider);
            } else {
                builder.connectString(properties.getConnectString());
            }
            CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
            curator.start();
            log.trace("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait() + properties.getBlockUntilConnectedUnit());
            curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
            log.trace("connected to zookeeper");
            return curator;
        }

        @Bean
        @ConditionalOnMissingBean
        public RetryPolicy exponentialBackoffRetry(ZookeeperProperties properties) {
            return new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(),
                    properties.getMaxRetries(),
                    properties.getMaxSleepMs());
        }
    }
}