/**
 * 
 */
package org.configframework.rose.admin.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.configframework.rose.admin.component.PageModel;
import org.configframework.rose.admin.dao.RoseConfigDao;
import org.configframework.rose.admin.dao.RoseConfigLogDao;
import org.configframework.rose.admin.dataobject.RoseConfigDo;
import org.configframework.rose.admin.dataobject.RoseConfigLogDo;
import org.configframework.rose.admin.dto.RoseConfigDto;
import org.configframework.rose.admin.dto.RoseConfigLogDto;
import org.configframework.rose.admin.enums.OperateEnum;
import org.configframework.rose.admin.service.RoseConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author yuantengkai
 * 配置项操作实现
 */
public class RoseConfigServiceImpl implements RoseConfigService{
	
	private static final Logger logger = LoggerFactory.getLogger(RoseConfigServiceImpl.class);
	
	@Resource
	private RoseConfigDao roseConfigDao;
	
	@Resource
	private RoseConfigLogDao roseConfigLogDao;
	
	@Resource
	private RoseConfigService roseConfigZkService;
	
	@Resource
	private TransactionTemplate transactionTemplate;

	@Override
	public boolean addConfig(final RoseConfigDto configDto) {
		if(configDto == null || StringUtils.isBlank(configDto.getConfigKey()) 
				|| StringUtils.isBlank(configDto.getConfigValue()) 
				|| StringUtils.isBlank(configDto.getAppName())){
			logger.warn("配置参数不合法或为空,"+configDto);
			return false;
		}
		final RoseConfigDo configEntity = new RoseConfigDo();
		BeanUtils.copyProperties(configDto, configEntity);
		final RoseConfigLogDo configLogEntity = buildRoseConfigLogEntity(null, configEntity, OperateEnum.ADD);
		boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {

			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				try {
					long configId = roseConfigDao.addConfig(configEntity);
					if(configId > 0){
						long configLogId = roseConfigLogDao.addRoseConfigLog(configLogEntity);
						if(configLogId > 0){
							boolean zkResult = roseConfigZkService.addConfig(configDto);
							if(zkResult){
								return true;
							}
							throw new Exception("新增配置项，zk操作失败");
						}
					}
					throw new Exception("新增配置项，db返回失败");
				} catch (SQLException e) {
					status.setRollbackOnly();
					logger.error("新增配置项db异常,"+configEntity,e);
				} catch (Exception e){
					status.setRollbackOnly();
					logger.error("新增配置项发生异常,"+configEntity,e);
				}
				return false;
			}
		});
		
		return result;
	}

	@Override
	public boolean updateConfig(final String key, final String value, final String operator) {
		if(StringUtils.isBlank(key)){
			logger.warn("配置参数key为空.");
			return false;
		}
		RoseConfigDo configDo = roseConfigDao.loadConfigByKey(key);
		if(configDo == null){
			logger.warn("配置项不存在,key:"+key);
			return false;
		}
		String lastValue = configDo.getConfigValue();
		configDo.setConfigValue(value);
		configDo.setOperator(operator);
		final RoseConfigLogDo logDo = buildRoseConfigLogEntity(lastValue, configDo, OperateEnum.UPDATE);
		boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {

			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				try{
					int num = roseConfigDao.updateConfigValueByKey(key, value, operator);
					if(num > 0){
						long configLogId = roseConfigLogDao.addRoseConfigLog(logDo);
						if(configLogId > 0){
							boolean zkResult = roseConfigZkService.updateConfig(key, value, operator);
							if(zkResult){
								return true;
							}
							throw new Exception("更新配置项，zk操作失败");
						}
					}
					throw new Exception("更新配置项，db返回失败");
				}catch(Exception e){
					status.setRollbackOnly();
					logger.error("更新配置项发生异常,key:"+key+",value:"+value,e);
				}
				return false;
			}
		});
		
		return result;
	}

	@Override
	public boolean deleteConfig(final String key, final String operator) {
		if(StringUtils.isBlank(key)){
			logger.warn("配置参数key为空.");
			return false;
		}
		RoseConfigDo configDo = roseConfigDao.loadConfigByKey(key);
		if(configDo == null){
			logger.warn("配置项不存在,key:"+key);
			return false;
		}
		String lastValue = configDo.getConfigValue();
		configDo.setConfigValue(null);
		configDo.setOperator(operator);
		final RoseConfigLogDo logDo = buildRoseConfigLogEntity(lastValue, configDo, OperateEnum.DELETE);
		boolean result = transactionTemplate.execute(new TransactionCallback<Boolean>() {

			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				try{
					int num = roseConfigDao.deleteByKey(key, operator);
					if(num > 0){
						long configLogId = roseConfigLogDao.addRoseConfigLog(logDo);
						if(configLogId > 0){
							boolean zkResult = roseConfigZkService.deleteConfig(key, operator);
							if(zkResult){
								return true;
							}
							throw new Exception("删除配置项，zk操作失败");
						}
					}
					throw new Exception("删除配置项，db返回失败");
				}catch(Exception e){
					status.setRollbackOnly();
					logger.error("删除配置项发生异常,key:"+key,e);
				}
				return false;
			}
		});
		
		return result;
	}
	
	@Override
	public PageModel<RoseConfigDto> pageQueryConfigList(int pageIndex,
			int pageSize, String appName, String key) {
		if(pageIndex < 1){
			pageIndex = 1;
		}
		PageModel<RoseConfigDo> daoResult = roseConfigDao.pageQueryConfigs(pageIndex, pageSize, appName, key);
		List<RoseConfigDo> doList = daoResult.getRecords();
		
		PageModel<RoseConfigDto> result = new PageModel<RoseConfigDto>(pageIndex, pageSize);
		List<RoseConfigDto> dtoList = new ArrayList<RoseConfigDto>();
		if(doList != null && doList.size() > 0){
			for(RoseConfigDo entity: doList){
				RoseConfigDto dto = new RoseConfigDto();
				BeanUtils.copyProperties(entity, dto);
				dtoList.add(dto);
			}
		}
		result.setRecords(dtoList);
		result.setRecordCount(daoResult.getRecordCount());
		return result;
	}
	
	@Override
	public PageModel<RoseConfigLogDto> pageQueryLogList(int pageIndex,
			int pageSize, String key,Date startTime, Date endTime) {
		if(pageIndex < 1){
			pageIndex = 1;
		}
		PageModel<RoseConfigLogDo> daoResult = roseConfigLogDao.pageQueryConfigLogs(pageIndex, pageSize, key, startTime,endTime);
		List<RoseConfigLogDo> doList = daoResult.getRecords();
		
		PageModel<RoseConfigLogDto> result = new PageModel<RoseConfigLogDto>(pageIndex, pageSize);
		List<RoseConfigLogDto> dtoList = new ArrayList<RoseConfigLogDto>();
		if(doList != null && doList.size() > 0){
			for(RoseConfigLogDo entity: doList){
				RoseConfigLogDto dto = new RoseConfigLogDto();
				BeanUtils.copyProperties(entity, dto);
				dtoList.add(dto);
			}
		}
		result.setRecords(dtoList);
		result.setRecordCount(daoResult.getRecordCount());
		return result;
	}
	
	private RoseConfigLogDo buildRoseConfigLogEntity(String lastValue, RoseConfigDo newConfigEntity,
			OperateEnum opt) {
		RoseConfigLogDo logEntity = new RoseConfigLogDo();
		logEntity.setAppName(newConfigEntity.getAppName());
		logEntity.setConfigKey(newConfigEntity.getConfigKey());
		logEntity.setConfigLastValue(lastValue);
		logEntity.setConfigNewValue(newConfigEntity.getConfigValue());
		logEntity.setOperator(newConfigEntity.getOperator());
		logEntity.setOptType(opt.name());
		return logEntity;
	}


}
