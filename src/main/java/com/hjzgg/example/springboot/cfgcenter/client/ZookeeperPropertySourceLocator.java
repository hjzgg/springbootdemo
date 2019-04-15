package com.hjzgg.example.springboot.cfgcenter.client;

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


/**
 * Zookeeper provides a <a href="http://zookeeper.apache.org/doc/current/zookeeperOver.html#sc_dataModelNameSpace">hierarchical namespace</a> that allows
 * clients to store arbitrary data, such as configuration data.  Spring Cloud Zookeeper Config is an alternative to the
 * <a href="https://github.com/spring-cloud/spring-cloud-config">Config Server and Client</a>.  Configuration is loaded into the Spring Environment during
 * the special "bootstrap" phase.  Configuration is stored in the {@code /config} namespace by default.  Multiple
 * {@code PropertySource} instances are created based on the application's name and the active profiles that mimicks the Spring Cloud Config
 * order of resolving properties.  For example, an application with the name "testApp" and with the "dev" profile will have the following property sources
 * created:
 *
 * <pre>{@code
 * config/testApp,dev
 * config/testApp
 * config/application,dev
 * config/application
 * }</pre>
 *
 * </p>
 * The most specific property source is at the top, with the least specific at the
 * bottom.  Properties is the {@code config/application} namespace are applicable to all applications
 * using zookeeper for configuration.  Properties in the {@code config/testApp} namespace are only available
 * to the instances of the service named "testApp".
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public class ZookeeperPropertySourceLocator {

    public static final String ZOOKEEPER_PREPERTY_SOURCE_NAME = "wmhcfg-zookeeper";

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

    public PropertySource getWmhCfgcenterPropertySource(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
            return env.getPropertySources().get(ZOOKEEPER_PREPERTY_SOURCE_NAME);
        }
        return null;
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
                    LOGGER.info("替换PropertySource " + ZOOKEEPER_PREPERTY_SOURCE_NAME);
                    env.getPropertySources().replace(ZOOKEEPER_PREPERTY_SOURCE_NAME, composite);
                } else {
                    LOGGER.info("添加PropertySource " + ZOOKEEPER_PREPERTY_SOURCE_NAME);
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
            LOGGER.info("wmhcfgcenter 刷新本地备份完成, path: " + backupDir);
        } catch (IOException e) {
            LOGGER.error("wmhcfgcenter 刷新本地备份异常..., path: " + backupDir, e);
        }
    }

    private PropertySource<CuratorFramework> create(String context) {
        ZookeeperPropertySource zps;
        if (ZKClient.isConnected()) {
            zps = new ZookeeperPropertySource(context, this.curator, false);
            this.backupZookeeperPropertySource(zps);
        } else {
            zps = new ZookeeperPropertySource(context, this.curator, true);
        }
        return zps;
    }
}
