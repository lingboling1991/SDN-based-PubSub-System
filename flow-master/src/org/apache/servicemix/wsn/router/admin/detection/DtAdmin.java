package org.apache.servicemix.wsn.router.admin.detection;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicemix.wsn.router.msg.udp.MsgHeart;
public class DtAdmin implements DtAction, IDt, Runnable {
	private long threshold = 45000;//ʧЧ��ֵ��ȱʡֵ
	private long sendPeriod = 10000;//����Ƶ�ʵ�ȱʡֵ
	private long scanPeriod = 15000;//ɨ��Ƶ�ʵ�ȱʡֵ
	private long synPeriod = 20000;//ͬ��Ƶ�ʵ�ȱʡֵ

	private HrtMsgHdlr AdminMgr;//����ģ��

	private Timer timer;//��ʱ��
	private DtTask scanTask;//ɨ������
	private DtTask sendTask;//��������
	private DtTask synTask;//ͬ������

	private ConcurrentHashMap<String, Long> tbl;//������Ϣ��keyΪĿ���ʶ��Ŀ���Ǽ�Ⱥ�Ļ�Ϊ��Ⱥ����Ŀ��Ϊ����Ļ�Ϊ���ַ����valueΪʱ���long��ʾ
	private ArrayBlockingQueue<MsgHeart> q;//������Ϣ�Ķ���

	public DtAdmin(HrtMsgHdlr AdminMgr) {
		this.AdminMgr = AdminMgr;
		
		tbl = new ConcurrentHashMap<String, Long>();

		q = new ArrayBlockingQueue<MsgHeart>(10);
		
		timer = new Timer();
	}

	public void setThreshold(long value) {
		threshold = value;
	}
	
	public void setSendPeriod(long value) {
		if (sendTask != null)
			sendTask.cancel();

		sendTask = new DtTask(this, SEND);
		timer.schedule(sendTask, sendPeriod, sendPeriod);
	}
	
	public void setScanPeriod(long value) {
		if (scanTask != null)
			scanTask.cancel();
		
		scanTask = new DtTask(this, SCAN);
		timer.schedule(scanTask, scanPeriod, scanPeriod);
	}
	
	public void setSynPeriod(long value) {
		if (synTask != null)
			synTask.cancel();
		
		synTask = new DtTask(this, SYN);
		timer.schedule(synTask, synPeriod, synPeriod);
	}
	
	
	public void action(int type) {
		// TODO Auto-generated method stub
		if (type == SCAN)
			scanAction();
		else if (type == SEND)
			sendAction();
		else
			synAction();
	}

	private void synAction() {
		// TODO Auto-generated method stub
		AdminMgr.synTopoInfo();
	}

	private void scanAction() {
		Date cur = new Date();
		if(!tbl.isEmpty()){
			for (String in : tbl.keySet()) {
				Long temTime = tbl.get(in);
				System.out.println(temTime);
				System.out.println(cur.getTime());
				if (cur.getTime() - tbl.get(in) > threshold) {
					
					int BackupIsPrimary = AdminMgr.BackupIsPrimary();//�뱸�ݷ������������ӣ�֪̽�Է��Ƿ���������Ա
					if (BackupIsPrimary == 0) {//�Է�����������Ա
					
					//tell routing manager that some broker is timeout and remove the item
					tbl.remove(in);
					AdminMgr.lost(in);
					}
				}
			}
		}
	}

	private void sendAction() {
		AdminMgr.sendHrtMsg();
		
	}

	
	public void run() {
		// TODO Auto-generated method stub		
		try {
			while (true) {
				MsgHeart msg = q.take();
				tbl.put(msg.indicator, new Date().getTime());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	public void addTarget(String indicator) {
		// TODO Auto-generated method stub
		tbl.put(indicator, new Date().getTime() + 1000);//add 1 minute to the new target
	}

	
	public void onMsg(Object msg) {
		// TODO Auto-generated method stub
		MsgHeart heartMsg = (MsgHeart)msg;
		try {
			q.put(heartMsg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	
	public void removeTarget(String indicator) {
		// TODO Auto-generated method stub
		if (tbl.containsKey(indicator))
			tbl.remove(indicator);
	}

}
