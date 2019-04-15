package com.hjzgg.example.springboot.dao.mybatis.cfgcenter;

import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.SearchVO;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import java.util.List;

@Mapper
public interface WmhcfgMapper {
    String TABLE_NAME = "wmhcfg";

    @InsertProvider(type = WmhcfgProvider.class, method = "insertRecord")
    void insertRecord(WmhcfgRecord record);

    @UpdateProvider(type = WmhcfgProvider.class, method = "updateRecord")
    void updateRecord(WmhcfgRecord record);

    @Select("select * from " + TABLE_NAME + " where system_id=#{systemId} and app_id=#{appId} and group_id=#{groupId}")
    List<WmhcfgRecord> findRecords(SearchVO searchVO);
}
