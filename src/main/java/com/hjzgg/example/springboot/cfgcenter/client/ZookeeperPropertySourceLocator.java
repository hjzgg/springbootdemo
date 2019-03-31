package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;

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

    private static final String ZOOKEEPER_PREPERTY_SOURCE_NAME = "wmhcfg-zookeeper";

    private ZookeeperConfigProperties properties;

    private CuratorFramework curator;

    private static final Log log = LogFactory.getLog(ZookeeperPropertySourceLocator.class);

    public ZookeeperPropertySourceLocator(CuratorFramework curator, ZookeeperConfigProperties properties) {
        this.curator = curator;
        this.properties = properties;
    }

    public String getContext() {
        return this.properties.getContext();
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
                    env.getPropertySources().replace(ZOOKEEPER_PREPERTY_SOURCE_NAME, composite);
                }
                env.getPropertySources().addFirst(composite);
            } catch (Exception e) {
                if (this.properties.isFailFast()) {
                    ReflectionUtils.rethrowRuntimeException(e);
                } else {
                    log.warn("Unable to load zookeeper config from " + context, e);
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
    }

    private PropertySource<CuratorFramework> create(String context) {
        return new ZookeeperPropertySource(context, this.curator);
    }
}
