package org.configframework.rose.admin.enums;

/**
 * @author yuantengkai
 * 操作key类型
 *
 */
public enum OperateEnum {
	
	ADD(1,"新增key"),
	UPDATE(2,"更新key"),
	DELETE(3,"删除key");
	
	private int value;
	
	private String desc;
	
	private OperateEnum(int value, String desc){
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}
	


}
