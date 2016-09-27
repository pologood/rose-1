/**
 * 
 */
package org.configframework.rose.admin.service.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dto.RoseConfigDto;
import org.configframework.rose.admin.dto.RoseConfigLogDto;
import org.configframework.rose.admin.service.RoseConfigService;
import org.configframework.rose.core.Environment;
import org.configframework.rose.core.data.MetaData;
import org.configframework.rose.core.zk.ConfigZkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 *
 */
public class RoseConfigZkServiceImpl implements RoseConfigService{
	
	private static final Logger logger = LoggerFactory.getLogger(RoseConfigZkServiceImpl.class);
	
	private ConfigZkManager zkManager;
	
	public void init(){
		//zookeeper start
		String zkAddress = Environment.getZkAddress();
		if(StringUtils.isBlank(zkAddress)){
			throw new IllegalArgumentException("zkAddress is empty.");
		}
		MetaData metaData = new MetaData();
		metaData.setZkAddress(zkAddress);
		zkManager = new ConfigZkManager(metaData,null);
		try {
			zkManager.start();
		} catch (Exception e) {
			logger.error("ConfigZkManager start exception,zkAddress:"+zkAddress,e);
			throw new RuntimeException("ConfigZkManager start exception.",e);
		}
	}
	
	public void destroy(){
		if(zkManager != null){
			zkManager.stop();
		}
	}

	@Override
	public boolean addConfig(RoseConfigDto configDto) {
		try {
			zkManager.addData(configDto.getConfigKey(), configDto.getConfigValue());
			return true;
		} catch (Exception e) {
			logger.error("新增配置项,zk操作失败,"+configDto,e);
			return false;
		}
		
	}

	@Override
	public boolean updateConfig(String key, String value, String operator) {
		try {
			zkManager.setData(key, value);
			return true;
		} catch (Exception e) {
			logger.error("更新配置项,zk操作失败,key:"+key+",value:"+value,e);
			return false;
		}
	}

	@Override
	public boolean deleteConfig(String key, String operator) {
		try {
			zkManager.deleteData(key);
			return true;
		} catch (Exception e) {
			logger.error("删除配置项,zk操作失败,key:"+key,e);
			return false;
		}
	}

	@Override
	public PageModel<RoseConfigDto> pageQueryConfigList(int start,
			int pageSize, String appName, String key) {
		return null;
	}

	@Override
	public PageModel<RoseConfigLogDto> pageQueryLogList(int pageIndex,
			int pageSize, String key,Date startTime, Date endTime) {
		return null;
	}

}
