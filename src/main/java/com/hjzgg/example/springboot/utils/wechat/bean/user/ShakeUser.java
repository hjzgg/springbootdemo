package com.hjzgg.example.springboot.utils.wechat.bean.user;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

import java.io.Serializable;

public class ShakeUser
        extends BaseResult {
    private Data data;

    public static class Data
            implements Serializable {
        private static final long serialVersionUID = -1L;
        private Integer page_id;
        private String openid;
        private Integer poi_id;
        private Beacon_info beacon_info;

        public static class Beacon_info
                implements Serializable {
            private static final long serialVersionUID = 1905122L;
            private double distance;
            private Integer major;
            private Integer minor;
            private String uuid;

            public double getDistance() {
                return this.distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public Integer getMajor() {
                return this.major;
            }

            public void setMajor(Integer major) {
                this.major = major;
            }

            public Integer getMinor() {
                return this.minor;
            }

            public void setMinor(Integer minor) {
                this.minor = minor;
            }

            public String getUuid() {
                return this.uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }
        }

        public Integer getPage_id() {
            return this.page_id;
        }

        public void setPage_id(Integer page_id) {
            this.page_id = page_id;
        }

        public String getOpenid() {
            return this.openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public Integer getPoi_id() {
            return this.poi_id;
        }

        public void setPoi_id(Integer poi_id) {
            this.poi_id = poi_id;
        }

        public Beacon_info getBeacon_info() {
            return this.beacon_info;
        }

        public void setBeacon_info(Beacon_info beacon_info) {
            this.beacon_info = beacon_info;
        }
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
