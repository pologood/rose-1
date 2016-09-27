package org.configframework.rose.admin.dao;

import java.sql.SQLException;

import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dataobject.RoseConfigDo;

public interface RoseConfigDao {
	
	public long addConfig(RoseConfigDo configDo) throws SQLException;
	
	public int updateConfigValueByKey(String key, String value, String operator);
	
	public int deleteByKey(String key,String operator);
	
	public RoseConfigDo loadConfigByKey(String key);
	
	public PageModel<RoseConfigDo> pageQueryConfigs(int pageIndex, int pageSize, String appName, String key);

}
