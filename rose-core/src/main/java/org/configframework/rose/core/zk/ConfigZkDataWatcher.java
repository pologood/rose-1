/**
 * 
 */
package org.configframework.rose.core.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.configframework.rose.core.ZkDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai
 * zookeeper data watcher <br/>
 * data key:{projectName}.xxx.yyy.zzz exm: shop-business-web.xxx.yyy.zzz <br/>
 * nodePath:/{rootPath}/{projectName}/xxx.yyy.zzz <br/>
 * data value:上述nodePath节点上的数据
 */
public class ConfigZkDataWatcher implements CuratorWatcher{
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigZkDataWatcher.class);
	
	private CuratorFramework zkClient;
	
	private ZkDataListener dataListener;

	public ConfigZkDataWatcher(CuratorFramework zkClient,ZkDataListener dataListener){
		this.zkClient = zkClient;
		this.dataListener = dataListener;
	}

	@Override
	public void process(WatchedEvent event) throws Exception {
		
		if (zkClient.getState() != CuratorFrameworkState.STOPPED){
			if (event.getType() == EventType.NodeDataChanged){
				byte[] b = zkClient.getData().usingWatcher(this).forPath(event.getPath());
				if(dataListener != null){//说明是客户端
					dataListener.onDataChange(getKey(event.getPath()), new String(b,"utf-8"));
				} else {//说明是配置中心
					logger.warn("NodeDataChanged,key:"+getKey(event.getPath())+",new value:"+new String(b,"utf-8"));
				}
			} else if(event.getType() == EventType.NodeDeleted){
				if(dataListener != null){
					zkClient.checkExists().usingWatcher(this).forPath(event.getPath());
					dataListener.onDataDeleted(getKey(event.getPath()));
				} else {
					logger.warn("NodeDeleted,key:"+getKey(event.getPath()));
				}
			} else if (event.getType() == EventType.NodeCreated){
				byte[] b = zkClient.getData().usingWatcher(this).forPath(event.getPath());
				if(dataListener != null){
					dataListener.onDataChange(getKey(event.getPath()), new String(b,"utf-8"));
				} else {
					logger.warn("NodeCreated,key:"+getKey(event.getPath())+",with value:"+new String(b,"utf-8"));
				}
			}
		}
		
	}
	

	private String getKey(String keyPath){
		int i = keyPath.indexOf("/", 2);
		String tmp = keyPath.substring(i+1);
		return tmp.replaceAll("/", ".");
	}
	
	public static void main(String[] args) {
		String p = "/rootpath/shop-business-admin/sss.ssdd.dddd";
		String[] array = p.split("/");
		System.out.println(array.length);
		System.out.println(array[0]+"/"+array[1]+"/"+array[2]);
		System.out.println(array[2]+"."+array[3]);
		int i = p.lastIndexOf("/");
		System.out.println(p.substring(0,i));
	}
	

}
