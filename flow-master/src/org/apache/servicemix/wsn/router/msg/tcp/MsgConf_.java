package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class MsgConf_ implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//representative's information
	public String repAddr;//�����ַ
	
	public int tPort;//�����TCP�˿ں�
	
	//
	public int neighborSize;//�ӽڵ���Ŀ
	
	public String multiAddr;//�鲥��ַ
	
	public int uPort;//�鲥�˿ں�
	
	public int joinTimes;
	
	public long synPeriod;
	
	//below heart detection
	public long lostThreshold;//�ж�ʧЧ�ķ�ֵ
	
	public long scanPeriod;//ɨ������
	
	public long sendPeriod;//��������

}
