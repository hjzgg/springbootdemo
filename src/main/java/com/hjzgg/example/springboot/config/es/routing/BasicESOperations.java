package com.hjzgg.example.springboot.config.es.routing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;

public abstract class BasicESOperations<T extends BasicElasticEntity> implements ApplicationContextAware {
    private RouteElasticSearchOperations<T> routeElasticSearchOperations;

    private AutowireCapableBeanFactory autowireBeanFactory;

    @PostConstruct
    private void init() {
        Class<T> rawType = resolveRawType();
        if (!AnnotatedElementUtils.isAnnotated(rawType, Document.class)) {
            throw new IllegalStateException(String.format(
                    "ES实体 %s 缺少注解 %s ."
                    , ClassUtils.getQualifiedName(rawType)
                    , ClassUtils.getQualifiedName(Document.class))
            );
        }
        Document document = AnnotatedElementUtils.getMergedAnnotation(rawType, Document.class);
        String clientName = document.clientName();
        String indexName = document.indexName();
        String type = document.type();
        String mappingPath = document.mappingPath();
        this.routeElasticSearchOperations = this.postProcess(new RouteElasticSearchOperations<>(clientName, indexName, type, mappingPath, rawType));
    }

    public RouteElasticSearchOperations<T> ops() {
        return this.routeElasticSearchOperations;
    }

    private Class<T> resolveRawType() {
        ParameterizedType parameterizedType = resolveRawType(getClass());
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    private static ParameterizedType resolveRawType(Class<?> clazz) {
        Object genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            return (ParameterizedType) genericSuperclass;
        }
        return resolveRawType(clazz.getSuperclass());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.autowireBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    private <R> R postProcess(R object) {
        if (object == null) {
            return null;
        }
        Object result;
        try {
            this.autowireBeanFactory.autowireBean(object);
            result = this.autowireBeanFactory.initializeBean(object, object.toString());
        } catch (RuntimeException e) {
            Class<?> type = object.getClass();
            throw new RuntimeException("Could not postProcess " + object + " of type " + type, e);// NOSONAR
        }
        return (R) result;
    }
}