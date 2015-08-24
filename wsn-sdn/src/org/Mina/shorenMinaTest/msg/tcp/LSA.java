package org.Mina.shorenMinaTest.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

public class LSA extends WsnMsg implements Serializable{
	public int seqNum; // ���к�
	public int syn; // 0Ϊ��ͨLSA��1Ϊͬ��LSA
	public String originator; // ����Դ����
	public ArrayList<String> lostGroup; // ��ʧ��Ⱥ�����޶�ʧ��Ϊ��
	public ArrayList<String> subsTopics; // ����Դ�Ķ���
	public ArrayList<String> cancelTopics; //����Դȡ���Ķ���
	public  ConcurrentHashMap<String, DistBtnNebr> distBtnNebrs; // ����Դ���ھӵľ���
	public long sendTime; //����ʱ��
	
	public void initLSA() {
		this.lostGroup = new ArrayList<String>();
		this.subsTopics = new ArrayList<String>();
		this.cancelTopics = new ArrayList<String>();
		this.distBtnNebrs = new ConcurrentHashMap<String, DistBtnNebr>();
	}
	
	private ArrayList<String> getForwardIp(){

		return Start.forwardIP=searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}
	
    public void processRegMsg(IoSession session){
		
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}
	
	public void processRepMsg(IoSession session){
		
		ArrayList<String> forwardIp = getForwardIp();
		//���Կ��λ�ã��ɲ��Կ�������ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}
	
}
