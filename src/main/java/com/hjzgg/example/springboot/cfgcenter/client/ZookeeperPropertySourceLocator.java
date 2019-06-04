package com.hjzgg.example.springboot.cfgcenter.client;

import com.hjzgg.example.springboot.cfgcenter.utils.zk.ZKClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

import static com.hjzgg.example.springboot.cfgcenter.client.ZookeeperConfigProperties.APP_NAME;
import static com.hjzgg.example.springboot.cfgcenter.client.ZookeeperConfigProperties.BASE_BACKUP_DIR;

public class ZookeeperPropertySourceLocator {

    public static final String ZOOKEEPER_PREPERTY_SOURCE_NAME = "cfg-zookeeper";

    private ZookeeperConfigProperties properties;

    private CuratorFramework curator;

    private static Logger LOGGER = LoggerFactory.getLogger(ZookeeperPropertySourceLocator.class);

    public ZookeeperPropertySourceLocator(CuratorFramework curator, ZookeeperConfigProperties properties) {
        this.curator = curator;
        this.properties = properties;
    }

    public String getContext() {
        return this.properties.getContext();
    }

    public PropertySource getCfgcenterPropertySource(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        return env.getPropertySources().get(ZOOKEEPER_PREPERTY_SOURCE_NAME);
    }

    public void locate(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
            String context = properties.getContext();
            CompositePropertySource composite = new CompositePropertySource(ZOOKEEPER_PREPERTY_SOURCE_NAME);
            try {
                PropertySource propertySource = create(context);
                composite.addPropertySource(propertySource);
                if (null != env.getPropertySources().get(ZOOKEEPER_PREPERTY_SOURCE_NAME)) {
                    LOGGER.info("替换PropertySource: " + ZOOKEEPER_PREPERTY_SOURCE_NAME);
                    env.getPropertySources().replace(ZOOKEEPER_PREPERTY_SOURCE_NAME, composite);
                } else {
                    LOGGER.info("添加PropertySource: " + ZOOKEEPER_PREPERTY_SOURCE_NAME);
                    env.getPropertySources().addFirst(composite);
                }
            } catch (Exception e) {
                if (this.properties.isFailFast()) {
                    ReflectionUtils.rethrowRuntimeException(e);
                } else {
                    LOGGER.error("Unable to load zookeeper config from " + context, e);
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
    }

    private void backupZookeeperPropertySource(ZookeeperPropertySource zps) {
        String backupDir = BASE_BACKUP_DIR + this.properties.getContext();
        String backupFile = String.format("%s/%s", backupDir, APP_NAME + ".properties");
        File bakFile = new File(backupFile);
        StringBuilder data = new StringBuilder();
        for (String propertyName : zps.getPropertyNames()) {
            data.append(propertyName)
                    .append("=")
                    .append(zps.getProperty(propertyName))
                    .append(System.lineSeparator());
        }
        try {
            FileUtils.writeStringToFile(bakFile, data.toString(), Charsets.UTF_8);
            LOGGER.info("配置中心客户端刷新本地备份完成, path: " + backupDir);
        } catch (IOException e) {
            LOGGER.error("配置中心客户端刷新本地备份异常..., path: " + backupDir, e);
        }
    }

    private PropertySource<CuratorFramework> create(String context) {
        ZookeeperPropertySource zps;
        if (ZKClient.getInstance().isConnected()) {
            zps = new ZookeeperPropertySource(context, this.curator, false);
            this.backupZookeeperPropertySource(zps);
        } else {
            zps = new ZookeeperPropertySource(context, this.curator, true);
        }
        return zps;
    }
}
