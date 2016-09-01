/**
 * 
 */
package org.configframework.rose.core;

/**
 * @author yuantengkai
 * zk配置项变化监听器
 */
public interface ZkDataListener {
	
	/**
	 * 
	 * @param key
	 * @param value 为空字符串也代表数据变更;为null才表示该节点被删除
	 */
	public void onDataChange(String key,String value);
	
	public void onDataDeleted(String key);
	
}
