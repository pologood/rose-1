/**
 * 
 */
package org.configframework.rose.admin.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.component.PageSqlMapClientDaoSupport;
import org.configframework.rose.admin.dao.RoseConfigLogDao;
import org.configframework.rose.admin.dataobject.RoseConfigLogDo;

/**
 * @author yuantengkai
 *
 */
public class RoseConfigLogDaoImpl extends PageSqlMapClientDaoSupport<RoseConfigLogDo> implements RoseConfigLogDao{

	@Override
	public long addRoseConfigLog(RoseConfigLogDo logDo) throws SQLException {
		Long id = (Long) getSqlMapClientTemplate().insert("roseConfigLog.insert", logDo);
		if(id == null){
			return 0;
		}
		return id;
	}

	@Override
	public PageModel<RoseConfigLogDo> pageQueryConfigLogs(int pageIndex,
			int pageSize, String key,Date startTime, Date endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(!StringUtils.isBlank(key)){
			params.put("configKey", key);
		}
		if(startTime != null){
			params.put("startTime", startTime);
		}
		if(endTime != null){
			params.put("endTime", endTime);
		}
        return this.pageQuery(pageIndex, pageSize, params,
        		"roseConfigLog.getConfigLogCount", "roseConfigLog.getConfigLogPageList");
	}

}
