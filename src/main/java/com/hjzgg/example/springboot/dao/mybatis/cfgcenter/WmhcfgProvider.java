package com.hjzgg.example.springboot.dao.mybatis.cfgcenter;

import com.hjzgg.example.springboot.cfgcenter.utils.SqlUtils;
import org.apache.ibatis.jdbc.SQL;

public class WmhcfgProvider {
    public String insertRecord(WmhcfgRecord record) {
        return SqlUtils.createInsertSql(record, WmhcfgMapper.TABLE_NAME);
    }

    public String updateRecord(WmhcfgRecord record) {
        SQL sql = SqlUtils.createUpdateSql(record, WmhcfgMapper.TABLE_NAME);
        sql.WHERE("id=#{id}");
        return sql.toString();
    }
}
