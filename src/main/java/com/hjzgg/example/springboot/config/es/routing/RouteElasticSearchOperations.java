package com.hjzgg.example.springboot.config.es.routing;

import com.hjzgg.example.springboot.cfgcenter.utils.ResourceUtils;
import com.hjzgg.example.springboot.exception.ServiceException;
import com.hjzgg.example.springboot.utils.JacksonHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.elasticsearch.client.Requests.indicesExistsRequest;

public class RouteElasticSearchOperations<T extends BasicElasticEntity> {
    private static Logger LOGGER = LoggerFactory.getLogger(RouteElasticSearchOperations.class);

    protected static final ExecutorService SLICE_EXECUTOR = Executors.newCachedThreadPool(new ESOperationThreadFactory());

    //默认滚动查询每个批次返回的文档数量
    public static final int DEFAULT_SCROLL_SIZE = 5000;
    //默认滚动查询快照存活时间
    public static final long DEFAULT_SCROLL_KEEPALIVE = 1 * 10 * 60 * 1000L;

    private static final String KEYWORD = "keyword";

    private static final String DOT_KEYWORD = "." + KEYWORD;

    @Autowired
    private Map<String, Client> clientMap;

    //ES客户端Bean名称
    private String clientName;

    //ES存储索引信息
    protected String indexName;

    //ES存储type信息
    protected String type;

    //mapping信息路径
    private String mappingPath;

    //ES实体class信息
    protected Class<T> rawType;

    //ES实体对应的Mapping信息
    protected MappingMetaData mappingMetaData;

    /**
     * @param clientName  ES客户端Bean名称
     * @param indexName   ES存储索引信息
     * @param type        ES存储type信息
     * @param mappingPath mapping信息路径
     * @param rawType     ES实体class信息
     */
    public RouteElasticSearchOperations(
            String clientName
            , String indexName
            , String type
            , String mappingPath
            , Class<T> rawType
    ) {
        this.clientName = clientName;
        this.indexName = indexName;
        this.type = type;
        this.mappingPath = mappingPath;
        this.rawType = rawType;
    }

    /**
     * 初始化
     */
    @PostConstruct
    private void init() {
        Timer timer = new Timer();// NOSONAR
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!indexExists()) {
                    LOGGER.info(
                            String.format("ES实体 %s 索引 %s %s."
                                    , ClassUtils.getQualifiedName(RouteElasticSearchOperations.this.rawType)
                                    , RouteElasticSearchOperations.this.indexName
                                    , createIndex(getDefaultSettings(RouteElasticSearchOperations.this.rawType)) ? "已创建" : "已存在")
                    );
                    String mapping = ResourceUtils.readResourceWithClasspath(RouteElasticSearchOperations.this.mappingPath);
                    if (StringUtils.isNotBlank(mapping)) {
                        LOGGER.info(String.format("索引 %s Mapping信息操作 %s"
                                , RouteElasticSearchOperations.this.indexName
                                , RouteElasticSearchOperations.this.putMapping(mapping) ? "成功" : "失败")
                        );
                    }
                } else {
                    super.cancel();
                }
            }
        }, 0L, 1500L);


    }

    /**
     * 返回ES客户端实例
     */
    private Client client() {
        return Optional.ofNullable(this.clientMap.get(this.clientName))
                .orElseThrow(() -> new IllegalStateException(String.format("不存在ES客户端(%s)，请检查配置", this.clientName)));
    }

    /**
     * 完全匹配(term)查询按照真实字段类型处理
     */
    public String queryFieldNameForDealType(String field) {
        if (!this.checkKeywordType(field)) {
            return field + DOT_KEYWORD;
        } else {
            return field;
        }
    }

    /**
     * 判断Es实体字段类型是否为keyword
     */
    public boolean checkKeywordType(String field) {
        MappingMetaData mmd = this.resolveMappingMetaData();
        if (mmd == null) {
            return false;
        }
        try {
            Map<String, Object> properties = (Map<String, Object>) mmd.getSourceAsMap().get("properties");
            Map<String, Object> fieldMap = (Map<String, Object>) properties.get(field);
            Object realType = fieldMap.get("type");
            if (KEYWORD.equals(realType)) {
                return true;
            } else {
                return KEYWORD.equals(
                        Optional.ofNullable(fieldMap.get("fields"))
                                .map(obj -> ((Map<String, Object>) obj).get("keyword"))
                                .map(obj -> ((Map<String, Object>) obj).get("type"))
                                .orElse(null)
                );
            }
        } catch (Exception e) {
            LOGGER.error(String.format("(%s, %s) check field(%s) keyword type error...", this.indexName, this.type, field), e);
            return false;
        }
    }

    /**
     * 返回Es实体字段真实类型信息
     */
    public String getFieldType(String field) {
        MappingMetaData mmd = this.resolveMappingMetaData();
        if (mmd == null) {
            return null;
        }
        try {
            Map<String, Object> properties = (Map<String, Object>) mmd.getSourceAsMap().get("properties");
            Map<String, Object> fieldMap = (Map<String, Object>) properties.get(field);
            Object realType = fieldMap.get("type");
            return realType.toString();
        } catch (Exception e) {
            LOGGER.error(String.format("(%s, %s) get field(%s) real type error...", this.indexName, this.type, field), e);
            return null;
        }
    }

    /**
     * 根据 SearchParameter 构建 QueryBuilder
     */
    public QueryBuilder queryBuilderWithSearchParameter(SearchParameter searchParameter) {
        //条件查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!CollectionUtils.isEmpty(searchParameter.getFields())) {
            for (Map.Entry<String, Object> entry : searchParameter.getFields().entrySet()) {
                String value = entry.getValue().toString();
                String key = entry.getKey();
                if (StringUtils.isNotBlank(value)) {
                    queryBuilder.filter(QueryBuilders.termQuery(this.queryFieldNameForDealType(key), value));
                }
            }
        }

        // 范围查询
        if (!CollectionUtils.isEmpty(searchParameter.getRange())) {
            for (String rangeKey : searchParameter.getRange().keySet()) {
                Object[] rangeValue = searchParameter.getRange().get(rangeKey);
                if (rangeValue != null && rangeValue.length == 2) {
                    queryBuilder.filter(QueryBuilders.rangeQuery(rangeKey)
                            .from(rangeValue[0])
                            .to(rangeValue[1])
                            .includeLower(true)
                            .includeUpper(false)
                    );
                }
            }
        }
        return queryBuilder;
    }

    /**
     * 根据id查找
     */
    public Optional<T> find(String id) {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("find begin, class:[%s]、id:[%s]", className, id));
            GetResponse getResponse = this.client().prepareGet(this.indexName, this.type, id).get();
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
            LOGGER.error(String.format("find error, class:[%s]、id:[%s]", className, id), e);
            return Optional.empty();
        }
    }

    /**
     * 根据id批量查找
     */
    public List<T> batchFind(List<String> ids) {
        String className = ClassUtils.getShortName(this.rawType);
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.EMPTY_LIST;
        }
        try {
            LOGGER.info(String.format("batch find begin, class:[%s]", className));
            MultiGetRequestBuilder multiGetRequestBuilder = this.client().prepareMultiGet();
            MultiGetResponse multiGetResponse = multiGetRequestBuilder.add(this.indexName, this.type, ids).get();
            if (ArrayUtils.isEmpty(multiGetResponse.getResponses())) {
                LOGGER.info(String.format("batch find failed, class:[%s]、ids:[%s]", className, ids));
                return Collections.EMPTY_LIST;
            } else {
                return Arrays.stream(multiGetResponse.getResponses())
                        .parallel()
                        .map(itemResponse -> {
                            try {
                                GetResponse getResponse = itemResponse.getResponse();
                                String sourceDoc = getResponse.getSourceAsString();
                                T entity = JacksonHelper.getObjectMapper().readValue(sourceDoc, this.rawType);
                                LOGGER.info(String.format("find success, class:[%s]、source:[%s]、id:[%s]", className, sourceDoc, entity.getId()));
                                return entity;
                            } catch (Exception e) {// NOSONAR
                                return null;
                            }
                        })
                        .filter(entity -> Objects.nonNull(entity))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error(String.format("batch find error, class:[%s]", className), e);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 条件查找
     */
    public List<T> query(QueryBuilder queryBuilder) {
        String className = ClassUtils.getShortName(this.rawType);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        try {

            LOGGER.info(String.format("find begin, class:[%s]、index:[%s]、query:[%s]", className, this.indexName, sourceBuilder));

            SearchResponse response = this.client().prepareSearch(this.indexName)
                    .setTypes(this.type)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(queryBuilder)
                    .get();

            if (response.isTimedOut()) {
                LOGGER.info(String.format("find failed, class:[%s]、index:[%s]、query:[%s]", className, this.indexName, sourceBuilder));
                return Collections.emptyList();
            } else {
                List<T> list = new ArrayList<>();
                SearchHits sourceDoc = response.getHits();
                for (SearchHit hit : sourceDoc.getHits()) {
                    T entity = JacksonHelper.getObjectMapper().readValue(hit.getSourceAsString(), this.rawType);
                    list.add(entity);
                }
                return list;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("find error, class:[%s]、index:[%s]、query:[%s]", className, this.indexName, sourceBuilder), e);
            return Collections.emptyList();
        }
    }

    /**
     * 实体保存
     */
    public void save(T entity) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            if (StringUtils.isBlank(entity.getId())) {
                throw new IllegalArgumentException("主键ID不能为空");
            }
            LOGGER.info(String.format("save begin, index[%s]、class:[%s]、id:[%s]", this.indexName, className, entity.getId()));
            if (Objects.isNull(entity.getCreateTime())) {
                entity.setCreateTime(new Date());
            }
            if (Objects.isNull(entity.getUpdateTime())) {
                entity.setUpdateTime(new Date());
            }
            IndexRequestBuilder indexRequestBuilder = this.client().prepareIndex(this.indexName, this.type, entity.getId())
                    .setSource(JacksonHelper.getObjectMapper().writeValueAsString(entity));
            if (entity.getVersion() != null) {
                indexRequestBuilder.setVersion(entity.getVersion());
                indexRequestBuilder.setVersionType(VersionType.INTERNAL);
            }
            LOGGER.info(String.format("save success, index[%s]、class:[%s]、id:[%s]、get id[%s]", this.indexName, className, entity.getId(), indexRequestBuilder.execute().actionGet().getId()));
        } catch (Exception e) {
            LOGGER.error(String.format("save error, index[%s]、class:[%s]、entity:[%s]", this.indexName, className, entity), e);
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]保存异常...", className, entity.getId()), e);
        }
    }

    /**
     * 实体批量保存
     */
    public void batchSave(List<T> entities) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("batch save begin, class:[%s]", className));
            BulkRequestBuilder bulkRequest = this.client().prepareBulk();
            List<IndexRequestBuilder> indexRequestBuilders = entities.parallelStream()
                    .filter(entity -> StringUtils.isNotBlank(entity.getId()))
                    .map(entity -> {
                        if (Objects.isNull(entity.getCreateTime())) {
                            entity.setCreateTime(new Date());
                        }
                        if (Objects.isNull(entity.getUpdateTime())) {
                            entity.setUpdateTime(new Date());
                        }
                        try {
                            IndexRequestBuilder indexRequestBuilder = this.client()
                                    .prepareIndex(this.indexName, this.type, entity.getId())
                                    .setSource(JacksonHelper.getObjectMapper().writeValueAsString(entity));
                            if (entity.getVersion() != null) {
                                indexRequestBuilder.setVersion(entity.getVersion());
                                indexRequestBuilder.setVersionType(VersionType.INTERNAL);
                            }
                            return indexRequestBuilder;
                        } catch (Exception e) {// NOSONAR
                            LOGGER.error(String.format("实体[%s]批量保存异常...", entity), e);
                            return null;
                        }
                    })
                    .filter(indexRequestBuilder -> Objects.nonNull(indexRequestBuilder))
                    .collect(Collectors.toList());
            for (IndexRequestBuilder indexRequestBuilder : indexRequestBuilders) {
                bulkRequest.add(indexRequestBuilder);
            }
            bulkRequest.get();
            LOGGER.info(String.format("batch save success, class:[%s]", className));
        } catch (Exception e) {
            LOGGER.error(String.format("batch save error, class:[%s]", className), e);
            throw new ServiceException(String.format("批量ES实体[class=%s]保存异常...", className), e);
        }
    }

    /**
     * 实体更新
     */
    public void update(T entity) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("update begin, index[%s]、class:[%s]、id:[%s]", this.indexName, className, entity.getId()));
            entity.setUpdateTime(new Date());
            String sourceDoc = JacksonHelper.getObjectMapper().writeValueAsString(entity);
            IndexRequest indexRequest = new IndexRequest(this.indexName, this.type, entity.getId())
                    .source(sourceDoc);
            UpdateRequest updateRequest = new UpdateRequest(this.indexName, this.type, entity.getId())
                    .doc(sourceDoc)
                    .upsert(indexRequest)
                    .retryOnConflict(5);
            this.client().update(updateRequest).actionGet();
            LOGGER.info(String.format("update success, index[%s]、class:[%s]、id:[%s]", this.indexName, className, entity.getId()));
        } catch (Exception e) {
            LOGGER.error(String.format("update error, index[%s]、class:[%s]、entity:[%s]", this.indexName, className, entity), e);
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]更新异常...", className, entity.getId()), e);
        }
    }

    /**
     * 实体批量更新
     */
    public void batchUpdate(List<T> entities, boolean updateTime) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("batch update begin, class:[%s]", className));
            BulkRequest bulkRequest = new BulkRequest();
            List<UpdateRequest> updateRequests = entities.parallelStream()
                    .map(entity -> {
                        try {
                            if (updateTime) {
                                entity.setUpdateTime(new Date());
                            }
                            String sourceDoc = JacksonHelper.getObjectMapper().writeValueAsString(entity);
                            IndexRequest indexRequest = new IndexRequest(this.indexName, this.type, entity.getId())
                                    .source(sourceDoc);
                            UpdateRequest updateRequest = new UpdateRequest(this.indexName, this.type, entity.getId())
                                    .doc(sourceDoc)
                                    .upsert(indexRequest)
                                    .retryOnConflict(3);
                            return updateRequest;
                        } catch (Exception e) {
                            LOGGER.error(String.format("实体[%s]批量更新异常...", entity), e);
                            return null;
                        }
                    })
                    .filter(updateRequest -> Objects.nonNull(updateRequest))
                    .collect(Collectors.toList());
            for (UpdateRequest updateRequest : updateRequests) {
                bulkRequest.add(updateRequest);
            }
            this.client().bulk(bulkRequest).actionGet();
            LOGGER.info(String.format("batch update success, class:[%s]", className));
        } catch (Exception e) {
            LOGGER.error(String.format("batch update error, class:[%s]", className), e);
            throw new ServiceException(String.format("ES实体[class=%s]batch update异常...", className), e);
        }
    }

    /**
     * 根据id删除
     */
    public boolean delete(String id) throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("delete begin, entity:[%s]、id:[%s]", className, id));
            DeleteResponse deleteResponse = this.client().prepareDelete(this.indexName, this.type, id).get();
            if (deleteResponse.status() == RestStatus.OK) {
                LOGGER.info(String.format("delete success, entity:[%s]、id:[%s]", className, id));
                return true;
            } else {
                LOGGER.info(String.format("delete failed, entity:[%s]、id:[%s]", className, id));
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("delete error, entity:[%s]、id:[%s]", className, id), e);
            throw new ServiceException(String.format("ES实体[class=%s, id=%s]删除异常...", className, id), e);
        }
    }

    /**
     * 删除索引
     * 注：请谨慎使用
     */
    public boolean deleteIndex() throws ServiceException {
        String className = ClassUtils.getShortName(this.rawType);
        try {
            LOGGER.info(String.format("delete begin, entity:[%s]、index:[%s]", className, this.indexName));
            DeleteIndexResponse deleteIndexResponse = this.client().admin().indices().prepareDelete(this.indexName).get();
            if (deleteIndexResponse.isAcknowledged()) {
                LOGGER.info(String.format("delete success, entity:[%s]、index:[%s]", className, this.indexName));
                return true;
            } else {
                LOGGER.info(String.format("delete failed, entity:[%s]、index:[%s]", className, this.indexName));
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("delete error, entity:[%s]、index:[%s]", className, this.indexName), e);
            throw new ServiceException(String.format("索引[class=%s, index=%s]删除异常...", className, this.indexName), e);
        }
    }

    /**
     * 获取分片数量
     */
    public int getTotalShards() {
        GetSettingsResponse response = this.client().admin()
                .indices()
                .prepareGetSettings(this.indexName)
                .get();
        String totalShards = response.getSetting(this.indexName, "index.number_of_shards");
        LOGGER.info(String.format("ES当前index：%s，当前shard数：%s", this.indexName, totalShards));
        return Integer.parseInt(totalShards);
    }

    public void searchSliceScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer) {
        this.searchSliceScroll(queryBuilder, consumer, DEFAULT_SCROLL_SIZE, DEFAULT_SCROLL_KEEPALIVE);
    }

    public void searchSliceScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, int size) {
        this.searchSliceScroll(queryBuilder, consumer, size, DEFAULT_SCROLL_KEEPALIVE);
    }

    public void searchSliceScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, long keepAlive) {
        this.searchSliceScroll(queryBuilder, consumer, DEFAULT_SCROLL_SIZE, keepAlive);
    }

    /**
     * slice scroll
     */
    public void searchSliceScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, int size, long keepAlive) {
        int threadSize = this.getTotalShards();
        for (int i = 0; i < threadSize; ++i) {
            SliceBuilder sliceBuilder = new SliceBuilder(i, threadSize);
            SearchResponse scrollResp = this.client().prepareSearch(this.indexName)
                    .setTypes(this.type)
                    .setScroll(new TimeValue(keepAlive))
                    //对_doc进行排序(addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC))，ES底层优化，更快速地获取到数据
                    .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                    .setQuery(queryBuilder)
                    .slice(sliceBuilder)
                    .setSize(size)
                    .get();

            int shardCount = i;
            SLICE_EXECUTOR.submit(() -> {
                try {
                    SearchResponse tmpScrollResp = scrollResp;
                    SearchHit[] searchHits = tmpScrollResp.getHits().getHits();
                    String scrollId = StringUtils.EMPTY;
                    int counter = 0;
                    while (ArrayUtils.isNotEmpty(searchHits)) {
                        List<T> entities = new ArrayList<>(size);
                        for (SearchHit searchHit : searchHits) {
                            T entity = JacksonHelper.getObjectMapper()
                                    .readValue(searchHit.getSourceAsString(), this.rawType);
                            entities.add(entity);
                        }

                        //计数并消费
                        counter += entities.size();
                        consumer.accept(entities);
                        LOGGER.info(String.format("(%s %s %s %s)searching slice scroll", this.indexName, this.type, shardCount, counter));

                        scrollId = tmpScrollResp.getScrollId();
                        tmpScrollResp = this.client().prepareSearchScroll(scrollId)
                                .setScroll(new TimeValue(keepAlive))
                                .execute()
                                .actionGet();
                        searchHits = tmpScrollResp.getHits().getHits();
                    }
                    if (StringUtils.isNotBlank(scrollId)) {
                        ClearScrollResponse clearResp = this.client().prepareClearScroll().addScrollId(scrollId).get();
                        LOGGER.info(String.format("(%s %s %s %s)search slice scroll finished, clear scrollId is %s", this.indexName, this.type, shardCount, counter, clearResp.isSucceeded()));
                    } else {
                        LOGGER.info(String.format("(%s %s %s %s)search slice scroll finished", this.indexName, this.type, shardCount, counter));
                    }
                } catch (IOException e) {
                    LOGGER.error(String.format("(%s %s %s)search slice scroll error...", this.indexName, this.type, shardCount), e);
                }
            });
        }
    }

    public void searchScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer) {
        this.searchScroll(queryBuilder, consumer, DEFAULT_SCROLL_SIZE, DEFAULT_SCROLL_KEEPALIVE);
    }

    public void searchScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, int size) {
        this.searchScroll(queryBuilder, consumer, size, DEFAULT_SCROLL_KEEPALIVE);
    }

    public void searchScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, long keepAlive) {
        this.searchScroll(queryBuilder, consumer, DEFAULT_SCROLL_SIZE, keepAlive);
    }

    public void searchScroll(QueryBuilder queryBuilder, Consumer<List<T>> consumer, int size, long keepAlive) {
        try {
            LOGGER.info(String.format("(%s %s)search scroll begin...", this.indexName, this.type));
            SearchResponse scrollResp = this.client().prepareSearch(this.indexName)
                    .setTypes(this.type)
                    .setQuery(queryBuilder)
                    .setScroll(new TimeValue(keepAlive))
                    .setSize(size)
                    .execute()
                    .actionGet();
            SearchHit[] searchHits = scrollResp.getHits().getHits();
            String scrollId = StringUtils.EMPTY;
            while (ArrayUtils.isNotEmpty(searchHits)) {
                List<T> entities = new ArrayList<>(size);
                for (SearchHit searchHit : searchHits) {
                    T entity = JacksonHelper.getObjectMapper()
                            .readValue(searchHit.getSourceAsString(), this.rawType);
                    entities.add(entity);
                }
                consumer.accept(entities);
                scrollId = scrollResp.getScrollId();
                scrollResp = this.client()
                        .prepareSearchScroll(scrollId)
                        .setScroll(new TimeValue(keepAlive))
                        .execute()
                        .actionGet();
                searchHits = scrollResp.getHits().getHits();
            }
            if (StringUtils.isNotBlank(scrollId)) {
                ClearScrollResponse clearResp = this.client()
                        .prepareClearScroll()
                        .addScrollId(scrollId)
                        .get();
                LOGGER.info(String.format("(%s %s)search scroll finished, clear scrollId is %s", this.indexName, this.type, clearResp.isSucceeded()));
            } else {
                LOGGER.info(String.format("(%s %s)search scroll finished", this.indexName, this.type));
            }
        } catch (Exception e) {
            LOGGER.error(String.format("(%s %s)search scroll error...", this.indexName, this.type), e);
        }
    }

    /**
     * 获取mapping信息
     */
    private MappingMetaData resolveMappingMetaData() {
        if (this.mappingMetaData == null) {
            try {
                this.mappingMetaData = this.client()
                        .admin()
                        .cluster()
                        .prepareState()
                        .setIndices(this.indexName)
                        .execute()
                        .actionGet()
                        .getState()
                        .getMetaData()
                        .getIndices()
                        .get(this.indexName)
                        .getMappings()
                        .get(this.type);
            } catch (Exception e) {
                LOGGER.error(String.format("(%s %s)resolve mapping meta data error...", this.indexName, this.type), e);
            }
        }
        return this.mappingMetaData;
    }

    /**
     * 创建索引
     */
    private boolean createIndex(Object settings) {
        CreateIndexRequestBuilder createIndexRequestBuilder = this.client()
                .admin()
                .indices()
                .prepareCreate(this.indexName);
        if (settings instanceof String) {
            createIndexRequestBuilder.setSettings(String.valueOf(settings));
        } else if (settings instanceof Map) {
            createIndexRequestBuilder.setSettings((Map) settings);
        } else if (settings instanceof XContentBuilder) {
            createIndexRequestBuilder.setSettings((XContentBuilder) settings);
        }
        try {
            return createIndexRequestBuilder.execute().actionGet().isAcknowledged();
        } catch (Exception e) {
            LOGGER.error(String.format("索引(%s)创建失败，可能已经存在...", indexName), e);
            return false;
        }
    }

    /**
     * 创建Mapping
     */
    private boolean putMapping(Object mapping) {
        PutMappingRequestBuilder requestBuilder = this.client()
                .admin()
                .indices()
                .preparePutMapping(this.indexName)
                .setType(this.type);
        if (mapping instanceof String) {
            requestBuilder.setSource(String.valueOf(mapping));
        } else if (mapping instanceof Map) {
            requestBuilder.setSource((Map) mapping);
        } else if (mapping instanceof XContentBuilder) {
            requestBuilder.setSource((XContentBuilder) mapping);
        }

        try {
            boolean acknowledged = requestBuilder.execute().actionGet().isAcknowledged();
            if (acknowledged) {
                this.resolveMappingMetaData();
            }
            return acknowledged;
        } catch (Exception e) {
            LOGGER.error(String.format("Mapping(%s)创建失败...", indexName), e);
            return false;
        }
    }

    public SearchRequestBuilder getSearchRequestBuilder(QueryBuilder queryBuilder) {
        return this.client()
                .prepareSearch(this.indexName)
                .setTypes(this.type)
                .setQuery(queryBuilder);
    }

    /**
     * 构建SearchRequestBuilder
     */
    public SearchRequestBuilder getSearchRequestBuilderWithScrollSlice(int id, int max, QueryBuilder queryBuilder) {
        SliceBuilder sliceBuilder = new SliceBuilder(id, max);
        return this.client()
                .prepareSearch(this.indexName)
                .setTypes(this.type)
                .setScroll(new TimeValue(DEFAULT_SCROLL_KEEPALIVE))
                //对_doc进行排序(addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC))，ES底层优化，更快速地获取到数据
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setQuery(queryBuilder)
                .slice(sliceBuilder)
                .setSize(DEFAULT_SCROLL_SIZE);
    }

    /**
     * 构建SearchRequestBuilder
     */
    public SearchRequestBuilder getSearchRequestBuilderWithSearchParameter(SearchParameter searchParameter) {
        SearchRequestBuilder searchRequestBuilder = this.client()
                .prepareSearch(this.indexName)
                .setTypes(this.type)
                .setQuery(this.queryBuilderWithSearchParameter(searchParameter))
                .setSize(DEFAULT_SCROLL_SIZE);

        // 分页
        if (searchParameter.isPageable()) {
            Integer page = searchParameter.getPage();
            Integer pageSize = searchParameter.getPageSize();
            searchRequestBuilder.setFrom((page - 1) * pageSize).setSize(pageSize);
        }

        // 排序查询
        if (StringUtils.isNotBlank(searchParameter.getSort())) {
            SortOrder sortOrder = Optional.ofNullable(searchParameter.getSortOrder())
                    .map(SortOrder::fromString)
                    .orElse(SortOrder.ASC);
            searchRequestBuilder.addSort(searchParameter.getSort(), sortOrder);
        }

        return searchRequestBuilder;
    }

    /**
     * 构建SearchRequestBuilder
     */
    public SearchRequestBuilder getSearchRequestBuilderWithScroll(QueryBuilder queryBuilder) {
        return this.client()
                .prepareSearch(this.indexName)
                .setTypes(this.type)
                .setScroll(new TimeValue(DEFAULT_SCROLL_KEEPALIVE))
                //对_doc进行排序(addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC))，ES底层优化，更快速地获取到数据
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setQuery(queryBuilder)
                .setSize(DEFAULT_SCROLL_SIZE);
    }

    private <T> Map getDefaultSettings(Class<T> clazz) {
        Document document = AnnotatedElementUtils.getMergedAnnotation(clazz, Document.class);
        if (document.useServerConfig()) {
            return Collections.emptyMap();
        }

        return new MapBuilder<String, String>().put("index.number_of_shards", String.valueOf(document.shards()))
                .put("index.number_of_replicas", String.valueOf(document.replicas()))
                .put("index.refresh_interval", document.refreshInterval())
                .put("index.store.type", document.indexStoreType()).map();
    }

    /**
     * 判断索引是否存在
     */
    public boolean indexExists() {
        return this.client()
                .admin()
                .indices()
                .exists(indicesExistsRequest(this.indexName))
                .actionGet()
                .isExists();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("clientName", clientName)
                .append("indexName", indexName)
                .append("type", type)
                .append("rawType", rawType)
                .toString();
    }

    static class ESOperationThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ESOperationThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "es-operation-pool-thread-";
        }

        public Thread newThread(Runnable r) {
            String name = namePrefix + threadNumber.getAndIncrement();
            Thread t = new Thread(
                    group
                    , r
                    , name
                    , 0
            );
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}