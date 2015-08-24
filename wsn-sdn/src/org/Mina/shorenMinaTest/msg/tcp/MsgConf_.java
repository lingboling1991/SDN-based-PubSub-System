package org.Mina.shorenMinaTest.msg.tcp;


import java.io.Serializable;
import java.util.ArrayList;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;


@SuppressWarnings("serial")
public class MsgConf_ extends WsnMsg implements Serializable {


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
