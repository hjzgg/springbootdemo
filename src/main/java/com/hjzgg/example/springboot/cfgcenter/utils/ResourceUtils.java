package com.hjzgg.example.springboot.cfgcenter.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author hujunzheng
 * @create 2018-06-26 10:23
 **/
public class ResourceUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtils.class);

    public static String readResourceWithClasspath(String path) {
        Resource resource = getClassPathResource(path);
        try {
            String content = IOUtils.toString(resource.getInputStream(), "UTF-8");
            return content;
        } catch (IOException e) {
            LOGGER.error("加载resource异常， path = " + path, e);
        }
        return StringUtils.EMPTY;
    }

    public static Resource getClassPathResource(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        return resource;
    }

    public static Resource getResource(String location) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResource(location);
    }

    public static Resource[] getResources(String location) {
        return getResources(new String[]{location});
    }

    public static Resource[] getResources(String[] locations) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            try {
                resources.addAll(Arrays.asList(resolver.getResources(location)));
            } catch (Exception e) {
                LOGGER.error("不存在resource文件，path = " + location, e);
            }
        }
        return resources.toArray(new Resource[]{});
    }

    public static Optional<ResourcePropertySource> getResourcePropertySource(String classpath) {
        Resource resource = getClassPathResource(classpath);
        try {
            ResourcePropertySource propertySource = new ResourcePropertySource(resource);
            return Optional.of(propertySource);
        } catch (Exception e) {
            LOGGER.error("resourcePropertySource 资源加载异常，path = " + classpath, e);
        }
        return Optional.empty();
    }
}