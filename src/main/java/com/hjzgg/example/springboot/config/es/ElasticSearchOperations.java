package com.hjzgg.example.springboot.config.es;


import com.hjzgg.example.springboot.beans.es.BaseESEntity;
import com.hjzgg.example.springboot.beans.es.Document;
import com.hjzgg.example.springboot.config.es.routing.RouteElasticSearchOperations;
import com.hjzgg.example.springboot.exception.ServiceException;
import com.hjzgg.example.springboot.utils.JacksonHelper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.elasticsearch.client.Requests.indicesExistsRequest;

public abstract class ElasticSearchOperations<T extends BaseESEntity> {
    private static Logger LOGGER = LoggerFactory.getLogger(RouteElasticSearchOperations.class);

    @Autowired
    private Client client;

    private String indexName;
    private String type;

    private Class<T> rawType;

    @PostConstruct
    private void init() {
        this.rawType = resolveReturnedClassFromGenericType();
        this.resolveConfig();
        LOGGER.info(String.format("ES实体 %s 索引 %s %s.", ClassUtils.getQualifiedName(this.rawType), this.indexName, createIndexIfNotCreated() ? "创建成功" : "已存在"));
    }

    public Optional<T> find(String id) {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("find begin, class:[%s]、id:[%s]", className, id));
            GetResponse getResponse = client.prepareGet(this.indexName, this.type, id).get();
            if (!getResponse.isExists() || getResponse.isSourceEmpty()) {
                LOGGER.info(String.format("find failed, class:[%s]、id:[%s]", className, id));
                return Optional.empty();
            } else {
                String sourceDoc = getResponse.getSourceAsString();
                T entity = JacksonHelper.getObjectMapper().readValue(sourceDoc, this.rawType);
                LOGGER.info(String.format("find success, class:[%s]、source:[%s]、id:[%s]", className, sourceDoc, id));
                return Optional.ofNullable(entity);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("find error, class:[%s]、id:[%s]", className, id));
            return Optional.empty();
        }
    }

    public void save(T entity) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("save begin, class:[%s]、id:[%s]", className, entity.getId()));
            String nowTime = LocalDateTime.now().toString();
            entity.setCreateTime(nowTime);
            entity.setUpdateTime(nowTime);
            IndexRequestBuilder indexRequestBuilder = this.client.prepareIndex(this.indexName, this.type, entity.getId()).setSource(JacksonHelper.getObjectMapper().writeValueAsString(entity));
            if (entity.getVersion() != null) {
                indexRequestBuilder.setVersion(entity.getVersion());
                indexRequestBuilder.setVersionType(VersionType.EXTERNAL);
            }
            LOGGER.info(String.format("save success, class:[%s]、id:[%s]、get id[%s]", className, entity.getId(), indexRequestBuilder.execute().actionGet().getId()));
        } catch (Exception e) {
            LOGGER.error(String.format("save error, class:[%s]、entity:[%s]", className, entity));
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]保存异常...", className, entity.getId()), e);
        }
    }

    public void update(T entity) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("update begin, class:[%s]、id:[%s]", className, entity.getId()));
            entity.setUpdateTime(LocalDateTime.now().toString());
            String sourceDoc = JacksonHelper.getObjectMapper().writeValueAsString(entity);
            IndexRequest indexRequest = new IndexRequest(this.indexName, this.type, entity.getId())
                    .source(sourceDoc);
            UpdateRequest updateRequest = new UpdateRequest(this.indexName, this.type, entity.getId())
                    .doc(sourceDoc)
                    .upsert(indexRequest);
            client.update(updateRequest).actionGet();
            LOGGER.info(String.format("update success, class:[%s]、id:[%s]", className, entity.getId()));
        } catch (Exception e) {
            LOGGER.error(String.format("update error, class:[%s]、entity:[%s]", className, entity));
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]更新异常...", className, entity.getId()), e);
        }
    }

    public boolean delete(String id) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("delete begin, entity:[%s]、id:[%s]", className, id));
            DeleteResponse deleteResponse = client.prepareDelete(this.indexName, this.type, id).get();
            if (deleteResponse.status() == RestStatus.OK) {
                LOGGER.info(String.format("delete success, entity:[%s]、id:[%s]", className, id));
                return true;
            } else {
                LOGGER.info(String.format("delete failed, entity:[%s]、id:[%s]", className, id));
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("delete error, entity:[%s]、id:[%s]", className, id));
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]删除异常...", className, id), e);
        }
    }

    private void resolveConfig() {
        if (!AnnotatedElementUtils.isAnnotated(this.rawType, Document.class)) {
            throw new IllegalStateException(String.format("ES实体 %s 缺少注解 %s .", ClassUtils.getQualifiedName(this.rawType), ClassUtils.getQualifiedName(Document.class)));
        }
        Document document = AnnotatedElementUtils.getMergedAnnotation(rawType, Document.class);
        this.indexName = document.indexName();
        this.type = document.type();
    }

    private boolean createIndexIfNotCreated() {
        return indexExists(this.indexName) || createIndex(this.indexName, getDefaultSettings(this.rawType));
    }

    private boolean createIndex(String indexName, Object settings) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
        if (settings instanceof String) {
            createIndexRequestBuilder.setSettings(String.valueOf(settings));
        } else if (settings instanceof Map) {
            createIndexRequestBuilder.setSettings((Map) settings);
        } else if (settings instanceof XContentBuilder) {
            createIndexRequestBuilder.setSettings((XContentBuilder) settings);
        }
        return createIndexRequestBuilder.execute().actionGet().isAcknowledged();
    }

    private static <T> Map getDefaultSettings(Class<T> clazz) {
        Document document = AnnotatedElementUtils.getMergedAnnotation(clazz, Document.class);
        if (document.useServerConfig()) {
            return Collections.EMPTY_MAP;
        }

        return new MapBuilder<String, String>().put("index.number_of_shards", String.valueOf(document.shards()))
                .put("index.number_of_replicas", String.valueOf(document.replicas()))
                .put("index.refresh_interval", document.refreshInterval())
                .put("index.store.type", document.indexStoreType()).map();
    }

    private Class<T> resolveReturnedClassFromGenericType() {
        ParameterizedType parameterizedType = resolveReturnedClassFromGenericType(getClass());
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    private static ParameterizedType resolveReturnedClassFromGenericType(Class<?> clazz) {
        Object genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            return parameterizedType;
        }
        return resolveReturnedClassFromGenericType(clazz.getSuperclass());
    }

    public boolean indexExists(String indexName) {
        return client.admin().indices().exists(indicesExistsRequest(indexName)).actionGet().isExists();
    }
}
