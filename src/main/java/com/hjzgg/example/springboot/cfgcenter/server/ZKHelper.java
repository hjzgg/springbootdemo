package com.hjzgg.example.springboot.cfgcenter.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author hujunzheng
 * @create 2019-04-01 0:04
 **/
@Component
public class ZKHelper {
    private static CuratorFramework CURATOR;

    public static boolean createPath(String path) {
        try {
            if (null == CURATOR.checkExists().forPath(path)) {
                CURATOR.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setData(String path, byte[] data) {
        if (createPath(path)) {
            try {
                CURATOR.setData().forPath(path, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getData(String path) {
        if (createPath(path)) {
            try {
                return new String(CURATOR.getData().forPath(path), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return StringUtils.EMPTY;
    }

    @Autowired
    @Qualifier("zookeeperServerCurator")
    public void setClient(CuratorFramework CURATOR) {
        ZKHelper.CURATOR = CURATOR;
    }
}