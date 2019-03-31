/*
 * Copyright 2013-2016 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.KeeperException;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type.*;

/**
 * Class that registers a {@link TreeCache} for each context.
 * It publishes events upon element change in Zookeeper.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
public class ConfigWatcher implements Closeable, TreeCacheListener {

    private static final Log log = LogFactory.getLog(ConfigWatcher.class);

    private AtomicBoolean running = new AtomicBoolean(false);
    private String context;
    private CuratorFramework source;
    private HashMap<String, TreeCache> caches;

    public ConfigWatcher(String context, CuratorFramework source) {
        this.context = context;
        this.source = source;
    }

    public void start() {
        if (this.running.compareAndSet(false, true)) {
            this.caches = new HashMap<>();
            if (!context.startsWith("/")) {
                context = "/" + context;
            }
            try {
                TreeCache cache = TreeCache.newBuilder(this.source, context).build();
                cache.getListenable().addListener(this);
                cache.start();
                this.caches.put(context, cache);
                // no race condition since ZookeeperAutoConfiguration.curatorFramework
                // calls curator.blockUntilConnected
            } catch (KeeperException.NoNodeException e) {
                // no node, ignore
            } catch (Exception e) {
                log.error("Error initializing listener for context " + context, e);
            }
        }
    }

    @Override
    public void close() {
        if (this.running.compareAndSet(true, false)) {
            for (TreeCache cache : this.caches.values()) {
                cache.close();
            }
            this.caches = null;
        }
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) {
        TreeCacheEvent.Type eventType = event.getType();
        if (eventType == NODE_ADDED
                || eventType == NODE_REMOVED
                || eventType == NODE_UPDATED) {
            if (null != ZKClient.EVENT_PUBLISHER) {
                ZKClient.EVENT_PUBLISHER.publishEvent(
                        new RefreshEvent(this, event, getEventDesc(event))
                );
            }
        }
    }

    public String getEventDesc(TreeCacheEvent event) {
        StringBuilder out = new StringBuilder();
        out.append("type=").append(event.getType());
        out.append(", path=").append(event.getData().getPath());
        byte[] data = event.getData().getData();
        if (data != null && data.length > 0) {
            out.append(", data=").append(new String(data, Charset.forName("UTF-8")));
        }
        return out.toString();
    }
}
