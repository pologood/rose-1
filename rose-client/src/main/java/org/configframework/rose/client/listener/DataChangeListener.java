package org.configframework.rose.client.listener;

/**
 * 对外提供的配置项变化监听器
 * @author yuantengkai
 *
 */
public interface DataChangeListener {
	
	
	public void onChange(String key,String value);

}
