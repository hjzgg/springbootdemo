/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hjzgg.example.springboot.cfgcenter.client;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static com.hjzgg.example.springboot.cfgcenter.client.ZookeeperConfigProperties.APP_NAME;
import static com.hjzgg.example.springboot.cfgcenter.client.ZookeeperConfigProperties.BASE_BACKUP_DIR;


/**
 * {@link org.springframework.core.env.PropertySource} that stores properties
 * from Zookeeper inside a map. Properties are loaded upon class initialization.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public class ZookeeperPropertySource extends AbstractZookeeperPropertySource {

    private static Logger LOGGER = LoggerFactory.getLogger(ZookeeperPropertySource.class);

    private Map<String, String> properties = new LinkedHashMap<>();

    public ZookeeperPropertySource(String context, CuratorFramework source, boolean backup) {
        super(context, source);

        //加载本地配置
        if (backup) {
            String backupDir = BASE_BACKUP_DIR + this.getContext();
            String backupFile = String.format("%s/%s", backupDir, APP_NAME + ".properties");
            try {
                InputStream is = FileUtils.openInputStream(new File(backupFile));
                InputStreamReader isr = new InputStreamReader(is);
                Properties properties = new Properties();
                properties.load(isr);
                properties.forEach((k, v) -> this.properties.put((String) k, (String) v));
            } catch (Exception e) {
                LOGGER.error("配置中心客户端本地配置加载异常...", e);
            }

        }
        //加载远程配置
        else {
            findProperties(this.getContext(), null);
        }
    }

    @Override
    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    private byte[] getPropertyBytes(String fullPath) {
        try {
            byte[] bytes = null;
            try {
                bytes = this.getSource().getData().forPath(fullPath);
            } catch (KeeperException e) {
                if (e.code() != KeeperException.Code.NONODE) {
                    throw e;
                }
            }
            return bytes;
        } catch (Exception exception) {
            ReflectionUtils.rethrowRuntimeException(exception);
        }
        return null;
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = this.properties.keySet();
        return strings.toArray(new String[strings.size()]);
    }

    private void findProperties(String path, List<String> children) {
        try {
            LOGGER.info("entering findProperties for path: " + path);
            if (children == null) {
                children = getChildren(path);
            }
            if (children == null || children.isEmpty()) {
                return;
            }
            for (String child : children) {
                String childPath = path + "/" + child;
                List<String> childPathChildren = getChildren(childPath);

                byte[] bytes = getPropertyBytes(childPath);
                if (!ArrayUtils.isEmpty(bytes)) {
                    registerKeyValue(childPath, new String(bytes, Charset.forName("UTF-8")));
                }
                // Check children even if we have found a value for the current znode
                findProperties(childPath, childPathChildren);
            }
            LOGGER.info("leaving findProperties for path: " + path);
        } catch (Exception exception) {
            ReflectionUtils.rethrowRuntimeException(exception);
        }
    }

    private void registerKeyValue(String path, String value) {
        String key = sanitizeKey(path);
        LOGGER.info(String.format("配置中心客户端解析配置节点(%s)，数据{%s}", key, value));
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(value));
            properties.forEach((k, v) -> this.properties.put((String) k, (String) v));
        } catch (IOException e) {
            LOGGER.info(String.format("配置中心客户端解析配置节点(%s)异常...", key));
        }
    }

    private List<String> getChildren(String path) throws Exception {
        List<String> children = null;
        try {
            children = this.getSource().getChildren().forPath(path);
        } catch (KeeperException e) {
            if (e.code() != KeeperException.Code.NONODE) {
                throw e;
            }
        }
        return children;
    }

}
