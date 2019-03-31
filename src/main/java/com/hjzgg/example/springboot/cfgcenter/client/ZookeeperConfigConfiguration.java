package com.hjzgg.example.springboot.cfgcenter.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperConfigConfiguration implements
        EnvironmentPostProcessor, ApplicationListener<ApplicationEvent>, Ordered {

    private static final int order = Ordered.HIGHEST_PRECEDENCE + 10;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            System.setProperty("cmos.system.id", "wechat");
            System.setProperty("cmos.app.id", "wmhopenapi");
            System.setProperty("groupenv", "a");

            ZKClient.init(new ZookeeperProperties(), new ZookeeperConfigProperties());
            ZKClient.PROPERTY_SOURCE_LOCATOR.locate(environment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext ac = ((ContextRefreshedEvent) event).getApplicationContext();
            ZKClient.EVENT_PUBLISHER = ac;
        } else if (event instanceof RefreshEvent) {

        } else if (event instanceof ContextClosedEvent) {
            if (null != ZKClient.CONFIG_WATCHER) {
                ZKClient.CONFIG_WATCHER.close();
            }
        }
    }
}