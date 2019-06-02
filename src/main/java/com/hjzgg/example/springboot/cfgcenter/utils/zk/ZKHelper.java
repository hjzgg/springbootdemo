package com.hjzgg.example.springboot.cfgcenter.utils.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKHelper.class);

    public static boolean createPath(String path) {
        try {
            LOGGER.info(String.format("zk path(%s) 开始创建...", path));
            if (null == ZKClient.getInstance()
                    .getCf()
                    .checkExists()
                    .forPath(path)) {
                ZKClient.getInstance()
                        .getCf()
                        .create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("zk path 创建异常...", e);
        }
        return false;
    }

    public static boolean deletePath(String path) {
        try {
            LOGGER.info(String.format("zk path(%s) 开始删除...", path));
            if (null != ZKClient.getInstance()
                    .getCf()
                    .checkExists()
                    .forPath(path)) {
                ZKClient.getInstance()
                        .getCf()
                        .delete()
                        .deletingChildrenIfNeeded()
                        .forPath(path);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("zk path 删除异常...", e);
        }
        return false;
    }

    public static boolean setData(String path, byte[] data) {
        if (createPath(path)) {
            try {
                LOGGER.info(String.format("zk path(%s) 设置数据...", path));
                ZKClient.getInstance()
                        .getCf()
                        .setData()
                        .forPath(path, data);
                return true;
            } catch (Exception e) {
                LOGGER.error("zk path 设置数据异常...", e);
            }
        }
        return false;
    }

    public static String getData(String path) {
        if (createPath(path)) {
            try {
                return new String(
                        ZKClient.getInstance()
                                .getCf()
                                .getData()
                                .forPath(path)
                        , "UTF-8");
            } catch (Exception e) {
                LOGGER.error("zk path 获取数据异常...", e);
            }
        }
        return StringUtils.EMPTY;
    }
}