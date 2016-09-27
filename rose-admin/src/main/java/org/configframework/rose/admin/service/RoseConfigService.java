/**
 * 
 */
package org.configframework.rose.admin.service;

import java.util.Date;

import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dto.RoseConfigDto;
import org.configframework.rose.admin.dto.RoseConfigLogDto;

/**
 * @author yuantengkai
 * 配置项操作统一接口
 */
public interface RoseConfigService {
	
	public boolean addConfig(RoseConfigDto configDto);
	
	public boolean updateConfig(String key, String value, String operator);
	
	public boolean deleteConfig(String key, String operator);
	
	public PageModel<RoseConfigDto> pageQueryConfigList(int pageIndex, int pageSize, String appName, String key);
	
	public PageModel<RoseConfigLogDto> pageQueryLogList(int pageIndex, int pageSize, String key, Date startTime, Date endTime);

}
