package org.apache.servicemix.wsn.router.msg.udp;

import java.io.Serializable;

public class MsgHello implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	public String indicator;		//���ͼ�Ⱥ����
	public long helloInterval;		//����hello��Ϣ��ʱ����
	public long deadInterval;		//�ж��ڵ�ʧЧ��ʱ����
	
}
