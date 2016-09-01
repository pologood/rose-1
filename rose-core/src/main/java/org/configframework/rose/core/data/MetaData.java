/**
 * 
 */
package org.configframework.rose.core.data;

/**
 * @author yuantengkai
 *
 */
public class MetaData {
	
	private static final String defaultZkAddress = "127.0.0.1:2181";
	
	private String zkAddress = defaultZkAddress;
	
	private String rootPath = "/ROSEZK";

	public String getZkAddress() {
		return zkAddress;
	}

	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	

}
