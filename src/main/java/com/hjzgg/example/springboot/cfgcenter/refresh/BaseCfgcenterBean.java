package com.hjzgg.example.springboot.cfgcenter.refresh;

import com.google.common.eventbus.Subscribe;
import com.hjzgg.example.springboot.cfgcenter.annotation.ConfigField;
import com.hjzgg.example.springboot.cfgcenter.client.RefreshEvent;
import com.hjzgg.example.springboot.cfgcenter.utils.ConfigurationBinder;
import com.hjzgg.example.springboot.cfgcenter.utils.ResourceUtils;
import com.hjzgg.example.springboot.cfgcenter.utils.zk.ZKClient;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author hujunzheng
 * @create 2018-07-20 18:12
 **/
public abstract class BaseCfgcenterBean implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(BaseCfgcenterBean.class);

    @PostConstruct
    public void init() {
        //注册到时间总线中
        ZKClient.getInstance()
                .getAeb()
                .register(this);
    }

    /**
     * z
     * 绑定自身目标
     **/
    protected void doBind() {
        Class<? extends BaseCfgcenterBean> clazz = this.getClass();
        if (org.springframework.util.ClassUtils.isCglibProxy(this)) {
            clazz = (Class<? extends BaseCfgcenterBean>) AopUtils.getTargetClass(this);
        }
        BaseCfgcenterBean target = binding(clazz, this.getDefaultResourcePath());
        this.copyProperties(target);
    }

    private void copyProperties(BaseCfgcenterBean target) {
        ReflectionUtils.doWithFields(this.getClass(), field -> {
            field.setAccessible(true);
            field.set(this, field.get(target));
        }, field -> AnnotatedElementUtils.isAnnotated(field, ConfigField.class));
    }

    /**
     * 绑定其他目标
     *
     * @param clazz 目标类
     **/
    protected <T> T doBind(Class<T> clazz) {
        T target = binding(clazz, this.getDefaultResourcePath());
        if (target instanceof InitializingBean) {
            try {
                ((InitializingBean) target).afterPropertiesSet();
            } catch (Exception e) {
                LOGGER.error(String.format("属性初始化失败[afterPropertiesSet]， class=%s", ClassUtils.getSimpleName(clazz), e));
            }
        }
        return target;
    }

    private <T> T binding(Class<T> clazz, String defaultResourcePath) {
        Optional<PropertySource> propertySource = Optional.empty();

        //加载配置中心配置
        if (ZKClient.getInstance().isZkInit()) {
            propertySource = Optional.ofNullable(
                    ZKClient.getInstance()
                            .resolvePropertySource()
            );
        }
        //加载本地配置
        else {
            Optional<ResourcePropertySource> resourcePropertySource = ResourceUtils.getResourcePropertySource(defaultResourcePath);
            if (resourcePropertySource.isPresent()) {
                propertySource = Optional.ofNullable(resourcePropertySource.get());
            }
        }
        if (propertySource.isPresent()) {
            T target;
            try {
                target = ConfigurationBinder
                        .withPropertySources(propertySource.get())
                        .bind(clazz);
            } catch (Exception e) {
                LOGGER.error(String.format("属性绑定失败， class=%s", ClassUtils.getSimpleName(clazz)), e);
                return null;
            }
            return target;
        }
        return null;
    }


    @Override
    public void afterPropertiesSet() {
        Class<?> target = this.getClass();
        if (AopUtils.isAopProxy(this)) {
            target = AopUtils.getTargetClass(this);
        }
        LOGGER.info(String.format("%s->%s模块引入配置中心%s..."
                , this.getModuleName()
                , ClassUtils.getSimpleName(target)
                , (ZKClient.getInstance()
                        .isConnected() ? "生效" : "无效")
        ));
    }

    public String getModuleName() {
        return StringUtils.EMPTY;
    }

    @Subscribe
    public void listenRefreshEvent(RefreshEvent refreshEvent) {
        this.afterPropertiesSet();
        LOGGER.info(refreshEvent.getEventDesc());
        this.refresh();
    }

    //通过事件进行刷新
    protected void refresh() {
        this.doBind();
    }

    //获取本地配置默认路径
    protected abstract String getDefaultResourcePath();
}