package com.hjzgg.example.springboot.exception;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 全局环境变量工具类，提供一些常用获取配置项值的方法封装
 */
@Component
public final class ExceptionEnv implements EnvironmentAware {

    /**
     * 在 com.cmos.common.spring.DefaultApplicationContextInitializer 初始过程中设置该值
     */
    static Environment _sharedEnv = null;

    /**
     * 判断全局环境变量是否存在配置项
     *
     * @param key 配置项键名
     */
    public static boolean has(String key) {
        Assert.notNull(_sharedEnv);
        return _sharedEnv.containsProperty(key);
    }

    public static String getString(String key) {
        Assert.notNull(_sharedEnv);
        return _sharedEnv.getProperty(key);
    }

    public static String getString(String key, String defval) {
        Assert.notNull(_sharedEnv);
        return _sharedEnv.getProperty(key, defval);
    }

    public static int getInt(String key, int defval) {
        return Integer.parseInt(getString(key, String.valueOf(defval)).trim());
    }

    public static String[] getStringArray(String key) {
        return getStringArray(key, ErrorConstants.RE_DELIMERS);
    }

    public static String[] getStringArray(String key, String regexp) {
        return getString(key, "").split(regexp);
    }

    public static int[] getIntArray(String key) {
        return getIntArray(key, ErrorConstants.RE_DELIMERS);
    }

    public static int[] getIntArray(String key, String regexp) {
        String[] items = getStringArray(key, regexp);

        int index = 0;
        int[] result = new int[items.length];
        for (String item : items) {
            result[index++] = Integer.parseInt(item.trim());
        }
        return result;
    }

    public static Class<?> getClass(String key) throws ClassNotFoundException {
        return Class.forName(getString(key).trim());
    }

    public static Class[] getClassArray(String key) throws ClassNotFoundException {
        return getClassArray(key, ErrorConstants.RE_DELIMERS);
    }

    public static Class[] getClassArray(String key, String regexp) throws ClassNotFoundException {
        String[] items = getStringArray(key, regexp);

        int index = 0;
        Class[] result = new Class[items.length];
        for (String item : items) {
            result[index++] = Class.forName(item.trim());
        }

        return result;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key, "false"));
    }

    /**
     * 向全局Environment对象添加新配置源
     *
     * @param propertySource 配置源对象
     */
    public static void setPropertySource(PropertySource<?> propertySource) {
        if (_sharedEnv instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment _env = (ConfigurableEnvironment) _sharedEnv;
            if (!_env.getPropertySources().contains(propertySource.getName())) {
                _env.getPropertySources().addLast(propertySource);
            } else {
                _env.getPropertySources().replace(propertySource.getName(), propertySource);
            }
        }
    }

    public static boolean isLoaded() {
        return _sharedEnv != null;
    }

    @Override
    public void setEnvironment(Environment environment) {
        _sharedEnv = environment;
    }
}
