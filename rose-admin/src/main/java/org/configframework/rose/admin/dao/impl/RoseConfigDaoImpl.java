/**
 * 
 */
package org.configframework.rose.admin.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.component.PageSqlMapClientDaoSupport;
import org.configframework.rose.admin.dao.RoseConfigDao;
import org.configframework.rose.admin.dataobject.RoseConfigDo;

/**
 * @author yuantengkai
 * 
 */
public class RoseConfigDaoImpl extends PageSqlMapClientDaoSupport<RoseConfigDo> implements RoseConfigDao{
	
	private static final String defaultOperator = "sys";

	@Override
	public long addConfig(RoseConfigDo configDo) throws SQLException {
		Long id = (Long) getSqlMapClientTemplate().insert("roseConfig.insert",configDo);
		if(id == null){
			return 0;
		}
		return id;
	}

	@Override
	public int updateConfigValueByKey(String key, String value, String operator) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("configKey", key);
        params.put("configValue", value);
        if(StringUtils.isBlank(operator)){
        	operator = defaultOperator;
        }
        params.put("operator", operator);
        return getSqlMapClientTemplate().update("roseConfig.updateConfigValueByKey", params);
	}

	@Override
	public int deleteByKey(String key, String operator) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("configKey", key);
        if(StringUtils.isBlank(operator)){
        	operator = defaultOperator;
        }
        params.put("operator", operator);
        return getSqlMapClientTemplate().update("roseConfig.deleteByKey", params);
	}

	@Override
	public RoseConfigDo loadConfigByKey(String key) {
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("configKey", key);
		return (RoseConfigDo) getSqlMapClientTemplate().queryForObject("roseConfig.loadConfigByKey", params);
	}

	@Override
	public PageModel<RoseConfigDo> pageQueryConfigs(int pageIndex,
			int pageSize, String appName, String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(!StringUtils.isBlank(key)){
			params.put("configKey", key);
		}
        if(!StringUtils.isBlank(appName)){
        	params.put("appName", appName);
        }
        return this.pageQuery(pageIndex, pageSize, params,
        		"roseConfig.getConfigCount", "roseConfig.getConfigPageList");
	}

}
