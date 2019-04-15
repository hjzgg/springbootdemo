package com.hjzgg.example.springboot.cfgcenter.utils;

import com.hjzgg.example.springboot.cfgcenter.annotation.TableField;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtils {
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([^A-Z-])([A-Z])");

    public static String createInsertSql(Object obj, String tableName) {
        SQL sql = new SQL();
        sql.INSERT_INTO(tableName);
        ReflectionUtils.doWithFields(obj.getClass(), field -> {
            field.setAccessible(true);
            if (null == field.get(obj)) {
                return;
            }
            String value = field.get(obj).toString();
            if (StringUtils.isEmpty(value)) {
                return;
            }
            String fieldName = field.getName();
            String columnName = camelcaseToUnderscore(fieldName);
            TableField tableField = field.getAnnotation(TableField.class);
            if (!StringUtils.isEmpty(tableField.columnName())) {
                columnName = tableField.columnName();
            }
            sql.VALUES(columnName, String.format("\"%s\"", value));
        }, field -> field.isAnnotationPresent(TableField.class));
        return sql.toString();
    }

    public static SQL createUpdateSql(Object obj, String tableName) {
        SQL sql = new SQL();
        sql.UPDATE(tableName);
        ReflectionUtils.doWithFields(obj.getClass(), field -> {
            field.setAccessible(true);
            if (null == field.get(obj)) {
                return;
            }
            String value = field.get(obj).toString();
            if (StringUtils.isEmpty(value)) {
                return;
            }
            String fieldName = field.getName();
            String columnName = camelcaseToUnderscore(fieldName);
            TableField tableField = field.getAnnotation(TableField.class);
            if (!StringUtils.isEmpty(tableField.columnName())) {
                columnName = tableField.columnName();
            }
            sql.SET(String.format("%s=\"%s\"", columnName, value));
        }, field -> field.isAnnotationPresent(TableField.class));
        return sql;
    }

    /**
     * 驼峰转下划线
     */
    private static String camelcaseToUnderscore(String value) {
        if (value.isEmpty()) {
            return value;
        }
        Matcher matcher = CAMEL_CASE_PATTERN.matcher(value);
        if (!matcher.find()) {
            return value;
        }
        matcher = matcher.reset();
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1) + '_'
                    + StringUtils.uncapitalize(matcher.group(2)));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
