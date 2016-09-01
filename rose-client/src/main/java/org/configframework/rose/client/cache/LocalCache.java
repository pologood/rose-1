/**
 * 
 */
package org.configframework.rose.client.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
import org.configframework.rose.client.RoseException;
import org.configframework.rose.client.listener.DataChangeListener;
import org.configframework.rose.core.Environment;
import org.configframework.rose.core.ZkDataListener;
import org.configframework.rose.core.data.MetaData;
import org.configframework.rose.core.zk.ConfigZkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 *
 */
public class LocalCache implements ZkDataListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LocalCache.class);
	
	private static final LocalCache instance = new LocalCache();
	
	/**
	 * 配置中心所有的key-value
	 */
	private ConcurrentMap<String, String> kvCache;
	
	/**
	 * 各个应用所关心的datachange,key:projectName或appName
	 */
	private ConcurrentMap<String, CopyOnWriteArrayList<DataChangeListener>> changeListenerMap;
	
	private ConfigZkManager zkManager;
	
	private String zkAddress;
	
	private MetaData metaData = new MetaData();
	
	private LocalCache(){
		kvCache = new ConcurrentHashMap<String, String>(1024);
		changeListenerMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<DataChangeListener>>(64);
		//zookeeper start
		zkAddress = Environment.getZkAddress();
		if(StringUtils.isBlank(zkAddress)){
			throw new RoseException("zkAddress is empty.");
		}
		metaData.setZkAddress(zkAddress);
		zkManager = new ConfigZkManager(metaData,this);
		try {
			zkManager.start();
		} catch (Exception e) {
			logger.error("ConfigZkManager start exception,zkAddress:"+zkAddress,e);
			throw new RoseException("ConfigZkManager start exception.",e);
		}
	}
	
	public static LocalCache getInstance() {
		return instance;
	}
	
	public String getValue(String key){
		if(kvCache.containsKey(key)){
			return kvCache.get(key);
		}
		try {
			String v = zkManager.getData(key);
			if(v != null){
				kvCache.put(key, v);
			}
			return v;
		} catch (Exception e) {
			logger.error("zkManager.getData exception,key:"+key,e);
			throw new RoseException(e);
		}
	}
	
	public void addChangeListener(String appName, DataChangeListener listener){
		CopyOnWriteArrayList<DataChangeListener> listenerList = changeListenerMap.get(appName);
		if(listenerList == null){
			listenerList = new CopyOnWriteArrayList<DataChangeListener>();
			CopyOnWriteArrayList<DataChangeListener> existList = changeListenerMap.putIfAbsent(appName, listenerList);
			if(existList != null){
				listenerList = existList;
			}
		}
		listenerList.add(listener);
	}

	@Override
	public void onDataChange(String key, String value) {
		kvCache.put(key, value);
		String appName = getAppName(key);
		CopyOnWriteArrayList<DataChangeListener> listenerList = changeListenerMap.get(appName);
		if(listenerList != null && listenerList.size() > 0){
			for(DataChangeListener listener:listenerList){
				listener.onChange(key, value);
			}
		}
	}

	@Override
	public void onDataDeleted(String key) {
		kvCache.remove(key);
		String appName = getAppName(key);
		CopyOnWriteArrayList<DataChangeListener> listenerList = changeListenerMap.get(appName);
		if(listenerList != null && listenerList.size() > 0){
			for(DataChangeListener listener:listenerList){
				listener.onChange(key, null);
			}
		}
	}
	
	private String getAppName(String key){
		int i = key.indexOf(".");
		return key.substring(0,i);
	}
	
	public static void main(String[] args) {
//		String tmp = "shop-business-web.xxx.yyy.zzz";
//		System.out.println(getAppName(tmp));
	}

}
