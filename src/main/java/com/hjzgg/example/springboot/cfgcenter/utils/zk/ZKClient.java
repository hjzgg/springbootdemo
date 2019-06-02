package com.hjzgg.example.springboot.cfgcenter.utils.zk;

import com.google.common.eventbus.AsyncEventBus;
import com.hjzgg.example.springboot.cfgcenter.client.ConfigWatcher;
import com.hjzgg.example.springboot.cfgcenter.client.ZookeeperConfigProperties;
import com.hjzgg.example.springboot.cfgcenter.client.ZookeeperProperties;
import com.hjzgg.example.springboot.cfgcenter.client.ZookeeperPropertySourceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.concurrent.Executors;

/**
 * @author hujunzheng
 * @create 2019-04-01 1:22
 **/
public final class ZKClient {
    private static final Log log = LogFactory.getLog(ZKClient.class);
    private boolean zkInit = false;
    private static ZKClient zkClient;
    private AsyncEventBus aeb = new AsyncEventBus(Executors.newFixedThreadPool(8));
    private ApplicationEventPublisher aep;
    private ZookeeperPropertySourceLocator zpsl;
    private ConfigWatcher cw;
    private Environment env;
    private CuratorFramework cf;

    private ZKClient() {
    }

    public static ZKClient getInstance() {
        if (zkClient == null) {
            synchronized (ZKClient.class) {
                if (zkClient == null) {
                    zkClient = new ZKClient();
                }
            }
        }
        return zkClient;
    }

    public void init(ZookeeperProperties zp
            , ZookeeperConfigProperties zcp
            , ConfigurableApplicationContext cac
    ) throws Exception {
        this.aep = cac;
        this.env = cac.getEnvironment();
        CuratorFramework curator = curatorFramework(exponentialBackoffRetry(zp), zp);
        this.cf = curator;
        ZookeeperPropertySourceLocator propertySourceLocator = zookeeperPropertySourceLocator(curator, zcp);
        this.zpsl = propertySourceLocator;
        ConfigWatcher configWatcher = configWatcher(zcp, curator);
        configWatcher.start();
        this.cw = configWatcher;
        //刷新环境变量
        this.refreshEnvironment();

        this.zkInit = true;
    }

    private static ZookeeperPropertySourceLocator zookeeperPropertySourceLocator(
            CuratorFramework zookeeperClientCurator, ZookeeperConfigProperties properties) {
        return new ZookeeperPropertySourceLocator(zookeeperClientCurator, properties);
    }

    private ConfigWatcher configWatcher(ZookeeperConfigProperties zookeeperConfigProperties
            , CuratorFramework zookeeperClientCurator) {
        return new ConfigWatcher(zookeeperConfigProperties.getContext(), zookeeperClientCurator);
    }


    private CuratorFramework curatorFramework(RetryPolicy retryPolicy, ZookeeperProperties properties) throws Exception {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString(properties.getConnectString());
        CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
        curator.start();
        if (log.isTraceEnabled()) {
            log.trace("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait() + properties.getBlockUntilConnectedUnit());
        }
        curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
        if (log.isTraceEnabled()) {
            log.trace("connected to zookeeper");
        }
        return curator;
    }

    private RetryPolicy exponentialBackoffRetry(ZookeeperProperties properties) {
        return new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(),
                properties.getMaxRetries(),
                properties.getMaxSleepMs());
    }

    public boolean isConnected() {
        return cf.getZookeeperClient().isConnected();
    }

    public void refreshEnvironment() {
        this.zpsl.locate(this.env);
    }

    public PropertySource resolvePropertySource() {
        return this.zpsl.getCfgcenterPropertySource(this.env);
    }

    public AsyncEventBus getAeb() {
        return aeb;
    }

    public ApplicationEventPublisher getAep() {
        return aep;
    }

    public ConfigWatcher getCw() {
        return cw;
    }

    public CuratorFramework getCf() {
        return cf;
    }

    public boolean isZkInit() {
        return zkInit;
    }
}