package com.hjzgg.example.springboot.dao.mybatis.cfgcenter;

import com.hjzgg.example.springboot.cfgcenter.utils.SqlUtils;
import org.apache.ibatis.jdbc.SQL;

public class CfgProvider {
    public String insertRecord(CfgRecord record) {
        return SqlUtils.createInsertSql(record, CfgMapper.TABLE_NAME);
    }

    public String updateRecord(CfgRecord record) {
        SQL sql = SqlUtils.createUpdateSql(record, CfgMapper.TABLE_NAME);
        sql.WHERE("id=#{id}");
        return sql.toString();
    }
}
