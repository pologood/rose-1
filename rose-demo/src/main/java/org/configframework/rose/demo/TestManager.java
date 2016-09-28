package org.configframework.rose.demo;

import org.configframework.rose.client.util.RoseConfigUtil;

public class TestManager {
	
	private String name;
	
	private String password;
	
	public void init(){
		System.out.println(name);
		System.out.println(password);
		System.out.println(RoseConfigUtil.getValue("rose-demo.testName"));
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}
