package org.Mina.shorenMinaTest.msg.tcp;

import org.Mina.shorenMinaTest.handlers.Start;
import org.Mina.shorenMinaTest.mgr.base.SysInfo;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.ForwardMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.TCPForwardMsg;
import org.Mina.shorenMinaTest.router.searchRoute;
import org.apache.mina.core.session.IoSession;

import java.io.Serializable;
import java.util.ArrayList;

public class LSDB extends WsnMsg implements Serializable {

	public ArrayList<LSA> lsdb;

	public void initLSDB() {
		this.lsdb = new ArrayList<LSA>();
	}

	private ArrayList<String> getForwardIp() {

		return Start.forwardIP = searchRoute.calForwardIP("500:3:6:10:15:20:26", "m", Start.testMap);
	}

	public void processRegMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);

	}

	public void processRepMsg(IoSession session) {

		ArrayList<String> forwardIp = getForwardIp();
		//策略库的位置，由策略库来过滤ip
		ForwardMsg forwardMsg = new TCPForwardMsg(forwardIp, SysInfo.gettPort(), this);
		MsgQueueMgr.addTCPMsgInQueue(forwardMsg);
	}

}