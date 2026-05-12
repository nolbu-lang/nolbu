/**
 * 
 */
package com.cs.bcjis.comm;

import javax.annotation.Resource;

import com.ibatis.sqlmap.client.SqlMapClient;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;

public abstract class BcjisCommAbstractDAO extends EgovAbstractDAO {
    @Resource(name="bcjis.sqlMapClient")
    public void setSuperSqlMapClient(SqlMapClient sqlMapClient) {
        super.setSuperSqlMapClient(sqlMapClient);
    }
}
