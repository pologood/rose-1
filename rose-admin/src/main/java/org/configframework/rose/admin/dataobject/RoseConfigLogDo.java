/**
 * 
 */
package org.configframework.rose.admin.dataobject;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author yuantengkai
 *
 */
public class RoseConfigLogDo {
	
	private Long id;
	
	private Date addTime;
	
	private Date updateTime;
	
	private String appName;
	
	private String configKey;
	
	private String configLastValue;
	
	private String configNewValue;
	
	private String operator;
	
	private String optType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigLastValue() {
		return configLastValue;
	}

	public void setConfigLastValue(String configLastValue) {
		this.configLastValue = configLastValue;
	}

	public String getConfigNewValue() {
		return configNewValue;
	}

	public void setConfigNewValue(String configNewValue) {
		this.configNewValue = configNewValue;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}
	
	@Override
	public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
