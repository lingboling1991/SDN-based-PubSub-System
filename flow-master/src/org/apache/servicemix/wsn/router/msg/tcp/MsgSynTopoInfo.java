package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;

public class MsgSynTopoInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String originator;//����/��������Ա��ַ(ͬ����Ϣ����Դ)
	
	public GroupUnit syn_root_group;//���ڵ㼯Ⱥ
	
	
}
