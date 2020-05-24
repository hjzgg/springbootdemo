package com.hjzgg.example.springboot.utils.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author hujunzheng
 * @create 2020-05-24 9:30
 * <p>
 * 数据存储一致性哈希
 **/
public class DataConsistentHashing {

    /**
     * key为真实主机，value为存储的数据
     */
    private Map<RealNode, List<String>> realNodes;

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
    public DataConsistentHashing(List<String> servers, Integer virtualNodeNumber) {
        this.realNodes = new LinkedHashMap<>(servers.size());
        this.virtualNodes = new TreeMap<>();
        this.virtualNodeNumber = virtualNodeNumber;

        for (String server : servers) {
            this.realNodes.put(new RealNode(server), new ArrayList<>());
        }
        this.initHash();
    }

    /**
     * 初始化一致性哈希算法
     */
    private void initHash() {
        for (RealNode realNode : this.realNodes.keySet()) {
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(realNode, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", vn.realNode.server, vn.name));
                this.virtualNodes.put(hash, vn);
            }
        }
    }

    public void printDetail() {
        this.realNodes.forEach(
                (rn, data) -> System.out.println(String.format("主机 %s, 数据 %s", rn.server, data))
        );
    }

    /**
     * 添加真实主机
     */
    public void addRealNode(String server) {
        RealNode rn = new RealNode(server);
        if (!this.realNodes.containsKey(rn)) {
            this.realNodes.put(rn, new ArrayList<>());
            long minHash = Long.MAX_VALUE;
            long maxHash = Long.MIN_VALUE;
            // 1、添加虚拟节点
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(rn, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", vn.realNode.server, vn.name));
                this.virtualNodes.put(hash, vn);
                if (minHash > hash) {
                    minHash = hash;
                }
                if (maxHash < hash) {
                    maxHash = hash;
                }
            }

            // 2、重新哈希数据（对应的虚拟节点哈希范围[minHash, maxHash]）
            SortedMap<Long, VirtualNode> subMap = this.virtualNodes.subMap(minHash, maxHash + 1);
            if (!subMap.isEmpty()) {
                for (VirtualNode vn : subMap.values()) {
                    // 如果不是当前主机
                    if (vn.realNode.equals(rn)) {
                        continue;
                    }
                    List<String> data = realNodes.get(vn.realNode);
                    List<String> copyData = new ArrayList<>(data);
                    data.clear();
                    copyData.forEach(this::putServer);
                }
            }
        }
    }

    /**
     * 删除真实主机
     */
    public void removeRealNode(String server) {
        RealNode rn = new RealNode(server);
        if (this.realNodes.containsKey(rn)) {
            // 1、删除虚拟节点
            List<String> data = this.realNodes.remove(rn);
            for (int i = 0; i < virtualNodeNumber; ++i) {
                VirtualNode vn = new VirtualNode(rn, String.valueOf(i + 1));
                long hash = this.murmur3Hash(String.format("%s&&VN%s", rn.server, vn.name));
                this.virtualNodes.remove(hash);
            }

            // 2、重新哈希数据到其他主机
            data.forEach(this::putServer);
        }
    }

    /**
     * 将数据存储到对应的真实主机上
     */
    public void putServer(String key) {
        Long hash = this.murmur3Hash(key);
        SortedMap<Long, VirtualNode> subMap = this.virtualNodes.tailMap(hash);
        RealNode realNode;
        if (subMap.isEmpty()) {
            Long h = this.virtualNodes.firstKey();
            realNode = this.virtualNodes.get(h).realNode;
        } else {
            Long h = subMap.firstKey();
            realNode = subMap.get(h).realNode;
        }
        realNodes.get(realNode).add(key);
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
            return this.virtualNodes.get(h).realNode.server;
        } else {
            Long h = subMap.firstKey();
            return subMap.get(h).realNode.server;
        }
    }

    private class VirtualNode {
        /**
         * 真实主机
         */
        private RealNode realNode;

        /**
         * 虚拟节点名称
         */
        private String name;

        public VirtualNode(RealNode server, String name) {
            this.realNode = server;
            this.name = name;
        }

        public RealNode getRealNode() {
            return realNode;
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
            return Objects.equals(realNode, that.realNode) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.realNode, this.name);
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
                , "192.168.0.7"
                , "192.168.0.8"
                , "192.168.0.9"
                , "192.168.0.10"
        );

        DataConsistentHashing consistentHashing = new DataConsistentHashing(servers, 1000);

        List<String> keys = Arrays.asList(
                "192.168.0.1&&VN1", "192.168.0.1&&VN2"
                , "192.168.0.2&&VN1", "192.168.0.2&&VN2"
                , "192.168.0.3&&VN1", "192.168.0.3&&VN2"
                , "192.168.0.4&&VN1", "192.168.0.4&&VN2"
                , "192.168.0.5&&VN1", "192.168.0.5&&VN2"
                , "192.168.0.6&&VN1", "192.168.0.6&&VN2"
                , "192.168.0.7&&VN1", "192.168.0.7&&VN2"
                , "192.168.0.8&&VN1", "192.168.0.8&&VN2"
                , "192.168.0.9&&VN1", "192.168.0.9&&VN2"
                , "192.168.0.10&&VN1", "192.168.0.10&&VN2"
                , "192.168.1.1&&VN1", "192.168.1.1&&VN2"
                , "192.168.1.2&&VN1", "192.168.1.2&&VN2"
        );
        keys.forEach(consistentHashing::putServer);
        consistentHashing.printDetail();

        System.out.println("\n删除主机: 192.168.0.6");
        consistentHashing.removeRealNode("192.168.0.6");
        consistentHashing.printDetail();

        System.out.println("\n删除主机: 192.168.0.1");
        consistentHashing.removeRealNode("192.168.0.1");
        consistentHashing.printDetail();

        System.out.println("\n添加主机: 192.168.1.1");
        consistentHashing.addRealNode("192.168.1.1");
        consistentHashing.printDetail();

        System.out.println("\n添加主机: 192.168.0.6");
        consistentHashing.addRealNode("192.168.0.6");
        consistentHashing.printDetail();

        System.out.println("\n添加主机: 192.168.0.1");
        consistentHashing.addRealNode("192.168.0.1");
        consistentHashing.printDetail();
    }
}