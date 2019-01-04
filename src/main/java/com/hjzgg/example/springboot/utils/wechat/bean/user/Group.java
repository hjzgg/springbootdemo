package com.hjzgg.example.springboot.utils.wechat.bean.user;

import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;

import java.io.Serializable;
import java.util.List;

public class Group
        extends BaseResult {
    private GroupData group;
    private List<GroupData> groups;
    private Integer groupid;

    public GroupData getGroup() {
        return this.group;
    }

    public void setGroup(GroupData group) {
        this.group = group;
    }

    public List<GroupData> getGroups() {
        return this.groups;
    }

    public void setGroups(List<GroupData> groups) {
        this.groups = groups;
    }

    public Integer getGroupid() {
        return this.groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public static class GroupData
            implements Serializable {
        private String id;
        private String name;
        private Integer count;

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return this.count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
