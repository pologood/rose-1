/**
 * 
 */
package org.configframework.rose.client.util;

import org.configframework.rose.client.cache.LocalCache;
import org.configframework.rose.client.listener.DataChangeListener;

/**
 * @author yuantengkai
 * 获取配置工具类
 */
public class RoseConfigUtil {
	
	/**
	 * 对外提供的获取配置value的方法
	 * @param key {appName}.xxx.yyy.zzz
	 * @return
	 */
	public static String getValue(String key){
		return LocalCache.getInstance().getValue(key);
	}
	
	/**
	 * 对外提供的监听配置数据变化的方法
	 * @param appName key的前缀：应用项目名；<br/>
	 * 比如key: shop-business-web.xxx.yyy.zzz 则appName为shop-business-web<br/>
	 * 并不是所有配置项的变化都会收到通知，而是前缀为该appName的key会收到通知
	 * @param listener
	 */
	public static void addDataChangeListener(String appName, DataChangeListener listener){
		LocalCache.getInstance().addChangeListener(appName, listener);
	}

}
