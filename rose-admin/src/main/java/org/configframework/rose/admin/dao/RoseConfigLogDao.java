package org.configframework.rose.admin.dao;

import java.sql.SQLException;
import java.util.Date;

import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dataobject.RoseConfigLogDo;

public interface RoseConfigLogDao {
	
	public long addRoseConfigLog(RoseConfigLogDo logDo) throws SQLException;
	
	public PageModel<RoseConfigLogDo> pageQueryConfigLogs(int pageIndex, int pageSize, String key,Date startTime, Date endTime);

}
