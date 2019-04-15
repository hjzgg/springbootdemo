package com.hjzgg.example.springboot.cfgcenter.client;

import com.hjzgg.example.springboot.cfgcenter.utils.ConfigurationBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author hujunzheng
 * @create 2018-07-20 19:04
 **/
@Component
public class CfgcenterInit implements ApplicationContextInitializer<ConfigurableWebApplicationContext>, ApplicationListener<ApplicationEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(CfgcenterInit.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext ac = ((ContextRefreshedEvent) event).getApplicationContext();
            ZKClient.EVENT_PUBLISHER = ac;
        } else if (event instanceof RefreshEvent) {
            ZKClient.EVENT_BUS.post(event);
        } else if (event instanceof ContextClosedEvent) {
            if (null != ZKClient.CONFIG_WATCHER) {
                ZKClient.CONFIG_WATCHER.close();
            }
        }
    }

    @Override
    public void initialize(ConfigurableWebApplicationContext context) {
        try {
            Environment environment = context.getEnvironment();
            ZKClient.ENVIRONMENT = environment;
            ZookeeperProperties zookeeperProperties = ConfigurationBinder
                    .withPropertySources(environment)
                    .bind(ZookeeperProperties.class);
            if (!zookeeperProperties.isEnabled()) {
                LOGGER.info("未开启 wmhcfgcenter!");
                return;
            }
            ZKClient.init(zookeeperProperties, new ZookeeperConfigProperties());
            ZKClient.PROPERTY_SOURCE_LOCATOR.locate(environment);
        } catch (Exception e) {
            LOGGER.error("wmhcfgcenter environment post process error!", e);
        }
    }
}