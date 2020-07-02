package com.hjzgg.example.springboot.dao.mybatis.intercepts;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hujunzheng
 * @create 2018-09-03 18:13
 **/
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
        , @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
        , @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class SqlCostInterceptor implements Interceptor {

    private static Logger LOGGER = LoggerFactory.getLogger(SqlCostInterceptor.class);

    private static final SimpleDateFormat YMD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat HMS_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat YMD_HMS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();

        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) target;
        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;

            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = boundSql.getSql();

            // 格式化Sql语句，去除换行符，替换参数
            sql = formatSql(sql, boundSql);

            LOGGER.info("SQL：[" + sql + "]执行耗时[" + sqlCost + "ms]");
        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    @Override
    public void setProperties(Properties properties) {
    }

    private String formatSql(String sql, BoundSql boundSql) {
        // 输入sql字符串空判断
        if (sql == null || sql.length() == 0) {
            return "";
        }

        // 美化sql
        sql = beautifySql(sql);

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();

        // 不传参数的场景，直接把Sql美化一下返回出去
        if (parameterObject == null || CollectionUtils.isEmpty(parameterMappingList)) {
            return sql;
        }

        // 定义一个没有替换过占位符的sql，用于出异常时返回
        String sqlWithoutReplacePlaceholder = sql;

        try {
            if (parameterMappingList != null) {
                Class<?> parameterObjectClass = parameterObject.getClass();
                // 如果参数是StrictMap且Value类型为Collection，获取key="list"的属性，这里主要是为了处理<foreach>循环时传入List这种参数的占位符替换
                // 例如select * from xxx where id in <foreach collection="list">...</foreach>
                if (isStrictMap(parameterObjectClass)) {
                    DefaultSqlSession.StrictMap<Collection<?>> strictMap = (DefaultSqlSession.StrictMap<Collection<?>>) parameterObject;
                    if (isList(strictMap.get("list").getClass())) {
                        sql = handleListParameter(sql, strictMap.get("list"), parameterMappingList);
                    }
                } else if (isMap(parameterObjectClass)) {
                    // 如果参数是Map则直接强转，通过map.get(key)方法获取真正的属性值
                    // 这里主要是为了处理<insert>、<delete>、<update>、<select>时传入parameterType为map的场景
                    Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
                    sql = handleMapParameter(sql, boundSql, paramMap, parameterMappingList);
                } else {
                    // 通用场景，比如传的是一个自定义的对象或者八种基本数据类型之一或者String
                    sql = handleCommonParameter(sql, boundSql, parameterMappingList, parameterObjectClass, parameterObject);
                }
            }
        } catch (Exception e) {
            // 占位符替换过程中出现异常，则返回没有替换过占位符但是格式美化过的sql，这样至少保证sql语句比BoundSql中的sql更好看
            LOGGER.error("SQL 占位符替换过程中出现异常...", e);
            return sqlWithoutReplacePlaceholder;
        }
        return sql;
    }

    /**
     * 美化Sql
     */
    private String beautifySql(String sql) {
        sql = sql.replace("\n", " ")
                .replaceAll("\\s+", " ");
        return sql;
    }

    /**
     * 处理参数为List的场景
     */
    private String handleListParameter(String sql, Collection<?> parameterList, List<ParameterMapping> parameterMappingList) throws Exception {
        int parameterMapppingIndex = 0;
        if (!CollectionUtils.isEmpty(parameterList)) {
            for (Object parameterObject : parameterList) {
                Class<?> parameterObjectClass = parameterObject.getClass();
                Object propertyValue = null;
                // 只处理基本数据类型、基本数据类型的包装类、String这三种
                // 如果是复合类型也是可以的，不过复杂点且这种场景较少，写代码的时候要判断一下要拿到的是复合类型中的哪个属性
                if (isPrimitiveOrPrimitiveWrapper(parameterObjectClass)) {
                    String value = parameterObject.toString();
                    sql = sql.replaceFirst("\\?", value);
                } else if (parameterObjectClass.isAssignableFrom(String.class)) {
                    String value = "'" + parameterObject.toString() + "'";
                    sql = sql.replaceFirst("\\?", value);
                } else {
                    if (parameterMapppingIndex < parameterMappingList.size()) {
                        for (Field field : parameterObjectClass.getDeclaredFields()) {
                            if (parameterMapppingIndex < parameterMappingList.size()) {
                                ParameterMapping parameterMapping = parameterMappingList.get(parameterMapppingIndex);
                                String propertyName = parameterMapping.getProperty();
                                propertyName = propertyName.substring(propertyName.lastIndexOf('.') + 1);
                                if (field.getName().equals(propertyName)) {
                                    // 要获取Field中的属性值，这里必须将私有属性的accessible设置为true
                                    field.setAccessible(true);
                                    propertyValue = field.get(parameterObject);
                                    ++parameterMapppingIndex;
                                    sql = sql.replaceFirst("\\?", this.resolveParameter(propertyValue, parameterMapping.getJdbcType(), parameterMapping.getJavaType()));
                                }
                            }
                        }
                    }
                }
            }
        }

        return sql;
    }

    /**
     * 处理参数为Map的场景
     */
    private String handleMapParameter(String sql, BoundSql boundSql, Map<?, ?> paramMap, List<ParameterMapping> parameterMappingList) {
        MetaObject metaObject = SystemMetaObject.forObject(paramMap);
        for (ParameterMapping parameterMapping : parameterMappingList) {
            Object propertyName = parameterMapping.getProperty();
            Object propertyValue;
            if (paramMap.containsKey(propertyName)) {
                propertyValue = paramMap.get(propertyName);
            } else {
                try {
                    //处理 xxx.yyy 类型的key值
                    propertyValue = metaObject.getValue(propertyName.toString());
                } catch (Exception e) {
                    propertyValue = boundSql.getAdditionalParameter(propertyName.toString());
                }
            }
            sql = sql.replaceFirst("\\?", this.resolveParameter(propertyValue, parameterMapping.getJdbcType(), parameterMapping.getJavaType()));
        }

        return sql;
    }


    /**
     * 处理通用的场景
     */
    private String handleCommonParameter(
            String sql
            , BoundSql boundSql
            , List<ParameterMapping> parameterMappingList
            , Class<?> parameterObjectClass
            , Object parameterObject) {
        for (ParameterMapping parameterMapping : parameterMappingList) {
            Object propertyValue;
            // 基本数据类型或者基本数据类型的包装类，直接toString即可获取其真正的参数值，其余直接取paramterMapping中的property属性即可
            if (isPrimitiveOrPrimitiveWrapper(parameterObjectClass) || parameterObjectClass.isAssignableFrom(String.class)) {
                propertyValue = parameterObject;
            } else {
                String propertyName = parameterMapping.getProperty();
                try {
                    Field field = parameterObjectClass.getDeclaredField(propertyName);
                    // 要获取Field中的属性值，这里必须将私有属性的accessible设置为true
                    field.setAccessible(true);
                    propertyValue = field.get(parameterObject);
                } catch (Exception e) {
                    propertyValue = boundSql.getAdditionalParameter(propertyName);
                }
            }
            sql = sql.replaceFirst("\\?", this.resolveParameter(propertyValue, parameterMapping.getJdbcType(), parameterMapping.getJavaType()));
        }

        return sql;
    }


    /**
     * 是否基本数据类型或者基本数据类型的包装类
     */
    private boolean isPrimitiveOrPrimitiveWrapper(Class<?> parameterObjectClass) {
        return parameterObjectClass.isPrimitive() ||
                (parameterObjectClass.isAssignableFrom(Byte.class) || parameterObjectClass.isAssignableFrom(Short.class) ||
                        parameterObjectClass.isAssignableFrom(Integer.class) || parameterObjectClass.isAssignableFrom(Long.class) ||
                        parameterObjectClass.isAssignableFrom(Double.class) || parameterObjectClass.isAssignableFrom(Float.class) ||
                        parameterObjectClass.isAssignableFrom(Character.class) || parameterObjectClass.isAssignableFrom(Boolean.class));
    }


    /**
     * 是否DefaultSqlSession的内部类StrictMap
     */
    private boolean isStrictMap(Class<?> parameterObjectClass) {
        return ClassUtils.isAssignable(DefaultSqlSession.StrictMap.class, parameterObjectClass);
    }


    /**
     * 是否List的实现类
     */
    private boolean isList(Class<?> parameterObjectClass) {
        return ClassUtils.isAssignable(List.class, parameterObjectClass);
    }


    /**
     * 是否Map的实现类
     */
    private boolean isMap(Class<?> parameterObjectClass) {
        return ClassUtils.isAssignable(Map.class, parameterObjectClass);
    }

    /**
     * 根据类型替换参数
     * <p>
     * 仅作为数字和字符串两种类型进行处理，需要特殊处理的可以继续完善这里
     *
     * @param value
     * @param jdbcType
     * @param javaType
     * @return
     */
    private String resolveParameter(Object value, JdbcType jdbcType, Class javaType) {
        if (Objects.isNull(value)) {
            return "NULL";
        }
        String strValue = String.valueOf(value);
        if (jdbcType != null) {
            switch (jdbcType) {
                //数字
                case BIT:
                case TINYINT:
                case SMALLINT:
                case INTEGER:
                case BIGINT:
                case FLOAT:
                case REAL:
                case DOUBLE:
                case NUMERIC:
                case DECIMAL:
                    break;
                //日期
                case DATE:
                    strValue = "'" + YMD_FORMAT.format(value) + "'";
                    break;
                case TIME:
                    strValue = "'" + HMS_FORMAT.format(value) + "'";
                    break;
                case TIMESTAMP:
                    strValue = "'" + YMD_HMS_FORMAT.format(value) + "'";
                    break;
                //其他，包含字符串和其他特殊类型
                default:
                    strValue = "'" + strValue + "'";
            }
        } else if (isPrimitiveOrPrimitiveWrapper(javaType)) {
            //不加单引号
        } else if (value instanceof java.sql.Date) {
            strValue = "'" + YMD_FORMAT.format(value) + "'";
        } else if (value instanceof java.sql.Time) {
            strValue = "'" + HMS_FORMAT.format(value) + "'";
        } else if (value instanceof java.sql.Timestamp) {
            strValue = "'" + YMD_HMS_FORMAT.format(value) + "'";
        } else if (value instanceof java.util.Date) {
            strValue = "'" + YMD_HMS_FORMAT.format(value) + "'";
        } else {
            strValue = "'" + strValue + "'";
        }
        return strValue;
    }
}