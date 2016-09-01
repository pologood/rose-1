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

/**
 * @author yuantengkai
 * zookeeper data watcher <br/>
 * data key:{projectName}.xxx.yyy.zzz exm: shop-business-web.xxx.yyy.zzz <br/>
 * nodePath:/{rootPath}/{projectName}/xxx.yyy.zzz <br/>
 * data value:上述nodePath节点上的数据
 */
public class ConfigZkDataWatcher implements CuratorWatcher{
	
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
				dataListener.onDataChange(getKey(event.getPath()), new String(b,"utf-8"));
//				System.out.println("NodeDataChanged,key:"+getKey(event.getPath())+",new value:"+new String(b,"utf-8"));
			} else if(event.getType() == EventType.NodeDeleted){
				dataListener.onDataDeleted(getKey(event.getPath()));
//				System.out.println("NodeDeleted,key:"+getKey(event.getPath()));
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
		int i = p.indexOf("/", 2);
		String tmp = p.substring(i+1);
		System.out.println(tmp.replaceAll("/", "."));
	}
	

}
