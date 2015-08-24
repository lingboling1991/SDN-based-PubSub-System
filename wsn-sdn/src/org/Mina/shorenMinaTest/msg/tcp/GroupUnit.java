package org.Mina.shorenMinaTest.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

	//����group�Ļ�����Ϣ
	
	public class GroupUnit extends WsnMsg implements Serializable {

		public String name;//group������
		
		public int uPort;//udp socket�Ķ˿ں�
		
		public BrokerUnit rep;//��Ⱥ����
		
		public Date date;//����ʱ��
        public int tPort;
		
		public void initGroupUnit() {
			this.rep = new BrokerUnit();
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

