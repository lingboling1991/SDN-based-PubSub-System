package org.apache.servicemix.wsn.router.msg.udp;

import org.apache.servicemix.wsn.router.mgr.GroupUnit;

import java.io.Serializable;

public class MsgGroup implements Serializable {

	//当有集群插入到本集群时，代表转发此消息

	private static final long serialVersionUID = 1L;

	public String sender;//sender's group name

	public GroupUnit g = new GroupUnit();

}
