package com.hjzgg.example.springboot.utils.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hujunzheng
 * @create 2020-05-24 9:30
 * <p>
 * 主机映射一致性哈希
 **/
public class MapConsistentHashing {

    /**
     * 真实主机集合
     */
    private Set<RealNode> realNodes;

    /**
     * 虚拟节点数量
     */
    private Integer virtualNodeNumber;

    /**
     * 虚拟节点Map，key表示虚拟节点的hash值，value表示虚拟节点
     */
    private SortedMap<Long, VirtualNode> virtualNodes;

    /**
     * @param servers           真实主机列表
     * @param virtualNodeNumber 虚拟节点数量
     */
    public MapConsistentHashing(List<String> servers, Integer virtualNodeNumber) {
        this.realNodes = new LinkedHashSet<>(servers.size());
        this.virtualNodes = new TreeMap<>();
        this.virtualNodeNumber = virtualNodeNumber;

        for (String server : servers) {
            this.realNodes.add(new RealNode(server));
        }
        this.initHash();
    }

    /**
     * 初始化一致性哈希算法
     */
    private void initHash() {
        for (RealNode realNode : this.realNodes) {
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(realNode.server, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", vn.server, vn.name));
                this.virtualNodes.put(hash, vn);
            }
        }
    }

    /**
     * 添加真实主机
     */
    public void addRealNode(String server) {
        RealNode rn = new RealNode(server);
        if (this.realNodes.add(rn)) {
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(server, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", vn.server, vn.name));
                this.virtualNodes.put(hash, vn);
            }
        }
    }

    /**
     * 删除真实主机
     */
    public void removeRealNode(String server) {
        RealNode rn = new RealNode(server);
        if (this.realNodes.remove(rn)) {
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(server, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", vn.server, vn.name));
                this.virtualNodes.remove(hash);
            }
        }
    }

    private long murmur3Hash(String key) {
        return Math.abs(
                Hashing.murmur3_128()
                        .hashString(key, StandardCharsets.UTF_8)
                        .asLong()
        );
    }

    public String getServer(String key) {
        Long hash = this.murmur3Hash(key);
        SortedMap<Long, VirtualNode> subMap = this.virtualNodes.tailMap(hash);
        if (subMap.isEmpty()) {
            Long h = this.virtualNodes.firstKey();
            return this.virtualNodes.get(h).server;
        } else {
            Long h = subMap.firstKey();
            return subMap.get(h).server;
        }
    }

    private class VirtualNode {
        /**
         * 真实主机
         */
        private String server;

        /**
         * 虚拟节点名称
         */
        private String name;

        public VirtualNode(String server, String name) {
            this.server = server;
            this.name = name;
        }

        public String getServer() {
            return server;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof VirtualNode)) {
                return false;
            }
            VirtualNode that = (VirtualNode) o;
            return Objects.equals(server, that.server) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.server, this.name);
        }
    }

    private class RealNode {
        /**
         * 真实主机
         */
        private String server;

        /**
         * 标识是否可用
         */
        private Boolean available;

        public RealNode(String server) {
            this.server = server;
            this.available = true;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public Boolean getAvailable() {
            return available;
        }

        public void setAvailable(Boolean available) {
            this.available = available;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RealNode)) {
                return false;
            }
            RealNode realNode = (RealNode) o;
            return Objects.equals(server, realNode.server);
        }

        @Override
        public int hashCode() {
            return Objects.hash(server);
        }
    }

    public static void main(String[] args) {
        List<String> servers = Arrays.asList(
                "192.168.0.1"
                , "192.168.0.2"
                , "192.168.0.3"
                , "192.168.0.4"
                , "192.168.0.5"
                , "192.168.0.6"
        );

        MapConsistentHashing consistentHashing = new MapConsistentHashing(servers, 50);

        List<String> keys = Arrays.asList(
                "hello"
                , "world"
                , "thanks"
                , "good"
                , "welcome"
        );

        for (String key : keys) {
            System.out.println(String.format("%s 映射到主机 %s", key, consistentHashing.getServer(key)));
        }

        System.out.println("\n删除主机: 192.168.0.6");
        consistentHashing.removeRealNode("192.168.0.6");

        for (String key : keys) {
            System.out.println(String.format("%s 映射到主机 %s", key, consistentHashing.getServer(key)));
        }

        System.out.println("\n添加主机: 192.168.0.6");
        consistentHashing.addRealNode("192.168.0.6");

        for (String key : keys) {
            System.out.println(String.format("%s 映射到主机 %s", key, consistentHashing.getServer(key)));
        }
    }
}