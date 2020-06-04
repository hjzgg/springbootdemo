package com.hjzgg.example.springboot.dao.mybatis.intercepts;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class TableShardInterceptor implements Interceptor {
    private final static Logger logger = LoggerFactory.getLogger(TableShardInterceptor.class);

    // Mapper 映射 Table
    private static final Map<String, String> MAPPER2TABLE = new HashMap<>();

    static {
        MAPPER2TABLE.put("CfgMapper", "cfg");
    }

    private final static String BOUNDSQL_NAME = "delegate.boundSql";

    private final static String MAPPEDSTATEMENT_NAME = "delegate.mappedStatement";

    private final static String BOUNDSQL_SQL_NAME = "delegate.boundSql.sql";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 取出被拦截对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
        // 分离代理对象，从而形成多次代理
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue(BOUNDSQL_NAME);
        String executeSql = boundSql.getSql();

        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue(MAPPEDSTATEMENT_NAME);
        /**
         * id 示例
         * com.hjzgg.example.springboot.dao.mybatis.cfgcenter.CfgMapper.findRecords
         */
        String id = mappedStatement.getId();
        String[] mapperClassPaths = id.split("\\.");
        String mapperClassName = mapperClassPaths[mapperClassPaths.length - 2];
        if (MAPPER2TABLE.containsKey(mapperClassName)) {
            String tableName = MAPPER2TABLE.get(mapperClassName);
            // 根据分表规则替换成对应的表名
            String newTableName = "xxx";
            String newExecuteSql = executeSql.replace(tableName, newTableName);
            metaStatementHandler.setValue(BOUNDSQL_SQL_NAME, newExecuteSql);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}