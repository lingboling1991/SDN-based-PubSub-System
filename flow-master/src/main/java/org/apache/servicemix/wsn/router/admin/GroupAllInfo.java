package org.apache.servicemix.wsn.router.admin;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicemix.wsn.router.msg.tcp.GroupUnit;
import org.apache.servicemix.wsn.router.msg.tcp.MsgConf_;

public class GroupAllInfo extends AdminBase implements Serializable{
	/**
	 * ��ż�Ⱥ������Ϣ������������Ϣ������������Ա�򱸷ݹ���Ա����
	 * 
	 * */
	
	private static final long serialVersionUID = 1L;
	
	public  ConcurrentHashMap<String, MsgConf_> groupconfs;//�������м�Ⱥ��������Ϣ���������ӹ���Ա֮��ͬ��

	public   ConcurrentHashMap<String, GroupUnit> groups;//����:group��Ϣ����������group����Ϣ
	
	public  String sendFlag = "Ask";
	
	public GroupAllInfo(String sendflag){
		
		this.sendFlag = sendflag;
		this.groupconfs = new ConcurrentHashMap<String, MsgConf_>();
		this.groups = new ConcurrentHashMap<String, GroupUnit> ();
	}


}
