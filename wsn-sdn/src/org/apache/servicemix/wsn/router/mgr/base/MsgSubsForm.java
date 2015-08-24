package org.apache.servicemix.wsn.router.mgr.base;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MsgSubsForm{
	
	public String topicComponent;//��������
	
	public ConcurrentHashMap<String,MsgSubsForm> topicChildList; //��һ����Ϣ��keyΪ��Ϣ����
	
	public ArrayList<String> subs; //���Ĵ���Ϣ�ļ�Ⱥ,valueΪ�䶩��ʱ��
	
	public ArrayList<String> routeNext; //��Ը����Ƶ�ת����һ���ڵ�
	
	public String routeRoot; //������ת�����ĸ��ڵ�
	
	public MsgSubsForm() {
		topicChildList = new ConcurrentHashMap<String, MsgSubsForm>();
		subs = new ArrayList<String>();
		routeNext = new ArrayList<String>();
	}
	
}