package org.apache.servicemix.wsn.router.admin.base;

import java.net.Socket;

public abstract class AState {

	//����������Ϣ
	abstract public void sendHrt();
	
	//ʧЧ��������Ϊ���Ա�ʶʧЧ�����ַ���
	abstract public void lost(String indicator);
	
	//����ͬ����Ⱥ���ı����Ϣ
	abstract public void synTopoInfo();
	
	//�����յ�udp��Ϣ
	abstract public void processUdpMsg(Object msg);
	
	//�����յ���tcp����
	abstract public void processTcpMsg(Socket s);
	
}
