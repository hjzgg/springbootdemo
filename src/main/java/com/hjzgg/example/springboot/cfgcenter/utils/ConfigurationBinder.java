package com.hjzgg.example.springboot.cfgcenter.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import java.lang.annotation.Annotation;

/**
 * @author hujunzheng
 * @create 2018-07-03 18:01
 * <p>
 * 不强依赖ConfigurationProperties，进行配置注入
 **/
public class ConfigurationBinder<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBinder.class);

    private Binder binder;

    private ConfigurationBinder(MutablePropertySources mutablePropertySources) {
        this.binder = new Binder(
                ConfigurationPropertySources.from(mutablePropertySources)
                , new PropertySourcesPlaceholdersResolver(mutablePropertySources)
                , new DefaultFormattingConversionService()
        );
    }

    private ConfigurationBinder(PropertySources propertySources) {
        this.binder = new Binder(
                ConfigurationPropertySources.from(propertySources)
                , new PropertySourcesPlaceholdersResolver(propertySources)
                , new DefaultFormattingConversionService()
        );
    }

    private ConfigurationBinder(Environment environment) {
        this.binder = new Binder(
                ConfigurationPropertySources.get(environment)
                , new PropertySourcesPlaceholdersResolver(environment)
                , new DefaultFormattingConversionService()
        );
    }

    public static <T> ConfigurationBinder<T> withPropertySources(PropertySource<?>... propertySources) {
        MutablePropertySources mutablePropertySources = new MutablePropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            mutablePropertySources.addLast(propertySource);
        }
        return new ConfigurationBinder<>(mutablePropertySources);
    }

    public static <T> ConfigurationBinder<T> withPropertySources(Environment environment) {
        return new ConfigurationBinder<>(environment);
    }

    public static <T> ConfigurationBinder<T> withPropertySources(PropertySources propertySources) {
        return new ConfigurationBinder<>(propertySources);
    }

    public <T> void bind(T bean) {
        ConfigurationProperties annotation = getAnnotation(bean, ConfigurationProperties.class);
        Assert.state(annotation != null,
                () -> "Missing @ConfigurationProperties on " + bean);
        Validated validated = getAnnotation(bean, Validated.class);
        Annotation[] annotations = (validated != null)
                ? new Annotation[]{annotation, validated}
                : new Annotation[]{annotation};
        ResolvableType type = ResolvableType.forClass(bean.getClass());
        Bindable<?> target = Bindable.of(type).withExistingValue(bean)
                .withAnnotations(annotations);
        try {
            Validator validator = getValidator();
            BindHandler bindHandler = getBindHandler(annotation, validator);
            this.binder.bind(annotation.prefix(), target, bindHandler);
        } catch (Exception e) {
            LOGGER.error("对象属性绑定失败...", e);
        }
    }

    public <T> T bind(Class<T> beanClass) {
        T bean = BeanUtils.instantiateClass(beanClass);
        this.bind(bean);
        return bean;
    }

    private Validator getValidator() {
        javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return new SpringValidatorAdapter(validator);
    }

    private BindHandler getBindHandler(ConfigurationProperties annotation,
                                       Validator validator) {
        BindHandler handler = new IgnoreTopLevelConverterNotFoundBindHandler();
        if (annotation.ignoreInvalidFields()) {
            handler = new IgnoreErrorsBindHandler(handler);
        }
        if (!annotation.ignoreUnknownFields()) {
            UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
            handler = new NoUnboundElementsBindHandler(handler, filter);
        }
        handler = new ValidationBindHandler(handler, validator);
        return handler;
    }

    private <A extends Annotation> A getAnnotation(Object bean, Class<A> type) {
        A annotation = AnnotationUtils.findAnnotation(bean.getClass(), type);
        return annotation;
    }
}