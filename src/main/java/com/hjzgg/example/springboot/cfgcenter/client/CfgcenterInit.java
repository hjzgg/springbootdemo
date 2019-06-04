package com.hjzgg.example.springboot.cfgcenter.client;

import com.hjzgg.example.springboot.cfgcenter.utils.ConfigurationBinder;
import com.hjzgg.example.springboot.cfgcenter.utils.zk.ZKClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
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
            LOGGER.info("初始化配置中心客户端监听器...");
            ZKClient.getInstance()
                    .init();
        } else if (event instanceof RefreshEvent) {
            ZKClient.getInstance()
                    .getAeb()
                    .post(event);
        } else if (event instanceof ContextClosedEvent) {
            if (null != ZKClient.getInstance().getCw()) {
                ZKClient.getInstance()
                        .getCw()
                        .close();
            }
        }
    }

    @Override
    public void initialize(ConfigurableWebApplicationContext cac) {
        try {
            ZookeeperProperties zookeeperProperties = ConfigurationBinder
                    .withPropertySources(cac.getEnvironment())
                    .bind(ZookeeperProperties.class);
            if (!zookeeperProperties.isEnabled()) {
                LOGGER.info("未开启配置中心客戶端...");
                return;
            }
            ZKClient.getInstance()
                    .binding(
                            zookeeperProperties
                            , new ZookeeperConfigProperties()
                            , cac
                    );
        } catch (Exception e) {
            LOGGER.error("配置中心客户端初始化异常...", e);
        }
    }
}