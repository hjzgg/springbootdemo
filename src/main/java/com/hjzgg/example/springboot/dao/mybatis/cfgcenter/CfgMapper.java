package com.hjzgg.example.springboot.dao.mybatis.cfgcenter;

import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.SearchVO;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

@Mapper
public interface CfgMapper {
    String TABLE_NAME = "cfg";

    @InsertProvider(type = CfgProvider.class, method = "insertRecord")
    void insertRecord(CfgRecord record);

    @UpdateProvider(type = CfgProvider.class, method = "updateRecord")
    void updateRecord(CfgRecord record);

    @Select("select * from " + TABLE_NAME + " where system_id=#{systemId} and app_id=#{appId} and group_id=#{groupId}")
    List<CfgRecord> findRecords(SearchVO searchVO);
}
