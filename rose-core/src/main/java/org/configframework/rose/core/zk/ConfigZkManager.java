/**
 * 
 */
package org.configframework.rose.core.zk;


import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.configframework.rose.core.ZkDataListener;
import org.configframework.rose.core.data.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuantengkai 配置中心zk管理<br/>
 * data key:{projectName}.xxx.yyy.zzz exm: shop-business-web.xxx.yyy.zzz <br/>
 * nodePath:/{rootPath}/{projectName}/xxx.yyy.zzz <br/>
 * data value:上述nodePath节点上的数据
 */
public class ConfigZkManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ConfigZkManager.class);

	private static final String NameSpace = "ROSE";

	private static final int sessionTimeoutMs = 60000;
	
	private AtomicBoolean initFlag = new AtomicBoolean(false);

	private String rootPath = "/ROSEZK";

	private CuratorFramework zkClient;
	
	private ZkDataListener dataListener;
	
	private CuratorWatcher watcher;
	
	private MetaData metaData;
	
	public ConfigZkManager(MetaData metaData,ZkDataListener dataListener){
		this.metaData = metaData;
		this.dataListener = dataListener;
	}

	public void start() throws Exception {
		if(!initFlag.compareAndSet(false, true)){
			return;
		}
		String addr = metaData.getZkAddress();
		logger.warn("#########start connecting to zk...");
		Builder cBuilder = CuratorFrameworkFactory.builder()
				.connectString(addr).namespace(NameSpace)
				.retryPolicy(new RetryNTimes(10000, 3 * 1000))
				.connectionTimeoutMs(30 * 1000)
				.sessionTimeoutMs(sessionTimeoutMs);
		zkClient = cBuilder.build();
		zkClient.start();
		if (!StringUtils.isBlank(metaData.getRootPath())) {
			rootPath = metaData.getRootPath();
		}
		createNodePath(rootPath);
		watcher = new ConfigZkDataWatcher(zkClient,dataListener);
		zkClient.getConnectionStateListenable().addListener(
				new ConnectionStateListener() {
					@Override
					public void stateChanged(CuratorFramework client,
							ConnectionState newState) {
						if (newState == ConnectionState.LOST
								|| newState == ConnectionState.SUSPENDED) {// session
							// timeout or connection loss
							logger.warn("######zksession timeout or connection loss, begining to reConnect..."
									+ newState);
							reConnect();
						}
					}

				});

	}
	
	public void stop(){
		zkClient.close();
	}
	
	private void createNodePath(String nodePath) throws Exception{
		Stat stat = zkClient.checkExists().forPath(nodePath);
		if (stat == null) {
			String createPath = zkClient.create()
					.withMode(CreateMode.PERSISTENT).forPath(nodePath);
			if (!StringUtils.isBlank(createPath)) {
				logger.warn("zknode created," + nodePath);
			} else {
				logger.error("create zknode failed," + nodePath);
				throw new Exception("create zknode failed," + nodePath);
			}
		} else {
			if(logger.isInfoEnabled()){
				logger.info("zknode already exist," + nodePath);
			}
		}
		
	}

	private void reConnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2 * 1000);
						logger.warn("start reconnecting to zookeeper...");
						if (zkClient.getZookeeperClient()
								.blockUntilConnectedOrTimedOut()) {
							logger.warn("zookeeper reconnected success.");
							// 此时可能rootNode不存在,需要重新创建
							createNodePath(rootPath);
							break;
						}
					} catch (InterruptedException e) {
						logger.error("connect to zookeeper InterruptedException,", e);
						break;
					} catch (Exception e) {
						logger.error("connect to zookeeper unKnow Exception,",e);
						try {
							Thread.sleep(30 * 1000);
						} catch (InterruptedException e1) {
						}
					}
				}
			}
		}).start();

	}

	/**
	 * 新增一个配置项
	 * @param key {projectname}.keyname <br/>
	 * exm: shop-business-web.xxx.sss.ddd
	 * @param value
	 */
	public void addData(String key,String value) throws Exception{
		String keyPath = getKeyPath(key);
		String projectPath = getProjectPath(key);
		createNodePath(projectPath);//必须先确保 projectPath存在，不然直接创建keyPath会报错
		createNodePath(keyPath);
		
//		CuratorWatcher watcher = new ConfigZkDataWatcher(zkClient, projectPath);
		zkClient.getData().usingWatcher(watcher).forPath(keyPath);//data watch只能watch当前节点上的data
		
		_setData(keyPath, value);
	}
	
	/**
	 * 更新一个配置项
	 * @param key {projectname}.keyname <br/>
	 * exm: shop-business-web.xxx.sss.ddd
	 * @param value
	 * @throws Exception 
	 */
	public void setData(String key,String value) throws Exception{
		String keyPath = getKeyPath(key);
		_setData(keyPath, value);
	}
	
	/**
	 * 删除一个配置项
	 * @param key {projectname}.keyname <br/>
	 * exm: shop-business-web.xxx.sss.ddd
	 * @throws Exception 
	 */
	public void deleteData(String key) throws Exception{
		String keyPath = getKeyPath(key);
		_deleteData(keyPath);
	}
	
	public String getData(String key) throws Exception{
		String keyPath = getKeyPath(key);
		return _getData(keyPath);
	}
	
	private String _getData(String keyPath) throws Exception {
		Stat stat = zkClient.checkExists().forPath(keyPath);
		if(stat == null){
			return null;
		}
		byte[] b = zkClient.getData().usingWatcher(watcher).forPath(keyPath);
		if(b != null){
			return new String(b,"utf-8");
		}
		return null;
	}

	private void _deleteData(String keyPath) throws Exception {
		Stat stat = zkClient.checkExists().forPath(keyPath);
		if(stat == null){
			return;
		}
		zkClient.delete().forPath(keyPath);
		
	}

	private void _setData(String keyPath, String value) throws Exception {
		Stat stat = zkClient.checkExists().forPath(keyPath);
		if(stat == null){
			return;
		}
		zkClient.setData().forPath(keyPath, value.getBytes("utf-8"));
	}
	
	/**
	 * 
	 * @param key shop-business-web.sss.ddd.xxx
	 * @return /shop-business-web/sss.ddd.xxx
	 */
	private  String getKeyPath(String key){
		String tmp = key.replaceFirst("\\.", "/");
		return rootPath + "/"+tmp;
	}
	
	private String getProjectPath(String key){
		int i = key.indexOf(".");
		return rootPath + "/" + key.substring(0,i);
	}
	
	public static void main(String[] args) throws Exception {
		
		MetaData md = new MetaData();
		
		ConfigZkManager manager = new ConfigZkManager(md,null);
		manager.start();
		manager.addData("shop-business-web2.sss.yyy.xxx", "你好");
		manager.addData("shop-business-web2.sss.yyy.xxx2", "hello");
		Thread.sleep(3000);
		manager.setData("shop-business-web2.sss.yyy.xxx", "ni hao");
		Thread.sleep(3000);
		manager.getData("shop-business-web2.sss.yyy.xxx3");
		Thread.sleep(3000);
		manager.setData("shop-business-web2.sss.yyy.xxx2", "hi");
		Thread.sleep(3000);
		manager.deleteData("shop-business-web2.sss.yyy.xxx4");
		Thread.sleep(3000);
		manager.deleteData("shop-business-web2.sss.yyy.xxx2");
		Thread.sleep(5000);
		manager.stop();
		/**
		 * NodeDataChanged,key:shop-business-web2.sss.yyy.xxx,new value:你好
		NodeDataChanged,key:shop-business-web2.sss.yyy.xxx2,new value:hello
		NodeDataChanged,key:shop-business-web2.sss.yyy.xxx,new value:ni hao
		NodeDataChanged,key:shop-business-web2.sss.yyy.xxx2,new value:hi
		NodeDeleted,key:shop-business-web2.sss.yyy.xxx2
		 */
	}

}
