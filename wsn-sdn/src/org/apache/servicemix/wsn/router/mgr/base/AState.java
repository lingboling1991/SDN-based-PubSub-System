package org.apache.servicemix.wsn.router.mgr.base;

import java.net.Socket;

import org.apache.servicemix.wsn.router.msg.tcp.LSA;

public abstract class AState extends SysInfo {

	//���뵽����֮��
	abstract public void join();

	//���Ͷ�����Ϣ
	abstract public void sendSbp(Object msg);
	
	// ����hello��̽�ھ�
	abstract public void addNeighbor(String target);

	//ʧЧ��������Ϊ���Ա�ʶʧЧ�����ַ���
	abstract public void lost(String indicator);
	
	// ���ô����ͬ���ͼ��LSA��ʱ��
	abstract public void setClock(boolean isRep);

	//�����յ�udp��Ϣ
	abstract public void processUdpMsg(Object msg);

	//�����յ���tcp����
	abstract public void processTcpMsg(Socket s);

	//ת�����ھ�
	abstract public void sendObjectToNeighbors(Object obj);
	
	// ��Ⱥ���鲥LSA
	abstract public void spreadLSAInLocalGroup(LSA lsa);
	
	//�ڱ���Ⱥ���鲥
	abstract public void spreadInLocalGroup(Object obj);
	
	//���LSA
	abstract public boolean addLSAToLSDB(LSA lsa);
	
	// ͬ��LSA
	abstract public void synLSA();
}
