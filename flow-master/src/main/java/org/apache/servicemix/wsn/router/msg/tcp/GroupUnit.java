package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.Date;

public class GroupUnit implements Serializable {

	//保存group的基本信息

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String addr;//group的地址
	
    public String netmask; //group子网掩码
	
	public String name;//group的名字
	
	public int tPort;//group的TCP端口号
	
	public Date date;//加入时间
	
	public long id; //代表设置时间
	
	public int uPort; //group的UDP端口号
	
	public GroupUnit(String addr, int port, String name) {
		this.addr = addr;
		this.name = name;
		this.tPort = port;
		date = new Date();
	}
	
	public GroupUnit(String addr, String netmask, int tPort, int uPort, long id, String name) {
		this.addr = addr;
		this.netmask = netmask;
		this.name = name;
		this.tPort = tPort;
		this.uPort = uPort;
		this.id = id;
		date = new Date();
	}
	
	public GroupUnit() {
		
	}
	
}
