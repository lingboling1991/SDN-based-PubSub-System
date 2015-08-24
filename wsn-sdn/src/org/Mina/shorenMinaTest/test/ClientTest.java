package org.Mina.shorenMinaTest.test;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.mgr.RtMgr;
import org.Mina.shorenMinaTest.msg.tcp.*;
import org.Mina.shorenMinaTest.msg.tcp.MsgAdminChange;
import org.Mina.shorenMinaTest.msg.tcp.MsgInfoChange;
import org.Mina.shorenMinaTest.msg.tcp.MsgInsert;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;
import org.Mina.shorenMinaTest.queues.SingleChannelQueueThread;

/**
 *
 */
public class ClientTest {

	private static final Log log = LogFactory.getLog(ClientTest.class);
	private static List<WsnMsg> msgList = MsgGenerator.generateMsgs();
	
	RtMgr  mgr = RtMgr.getInstance();
	
	
	static NioSocketConnector connector = MinaUtil.createSocketConnector();	
	static ConnectFuture cf = connector.connect(new InetSocketAddress("10.109.253.41", 30001));//��������
	
	/*static NioDatagramConnector connector2 = MinaUtil.createDatagramConnector();	
	static ConnectFuture df = connector2.connect(new InetSocketAddress("10.108.166.217", 30002));//��������
	*/
	/*static NioSocketConnector connector3 = MinaUtil.createSocketConnector();	
	static ConnectFuture cf2 = connector3.connect(new InetSocketAddress("10.108.166.217", 30003));//��������
	
	static NioDatagramConnector connector4 = MinaUtil.createDatagramConnector();	
	static ConnectFuture df2 = connector4.connect(new InetSocketAddress("10.108.166.217", 30004));//��������
*/	
				
	//static NioSocketConnector connector1 = MinaUtil.createSocketConnector();	
	//static ConnectFuture cf1 = connector1.connect(new InetSocketAddress("10.109.253.17", 30001));//��������
	
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		cf.awaitUninterruptibly();//�ȴ����Ӵ������   
		//cf1.awaitUninterruptibly();//�ȴ����Ӵ������
		//df.awaitUninterruptibly();
		//df2.awaitUninterruptibly();
		/*				
		NioSocketConnector connector2 = MinaUtil.createSocketConnector();	
		ConnectFuture cf2 = connector2.connect(new InetSocketAddress("10.108.164.66", 30001));//��������
		cf2.awaitUninterruptibly();//�ȴ����Ӵ������ 
		
		NioSocketConnector connector3 = MinaUtil.createSocketConnector();	
		ConnectFuture cf3 = connector3.connect(new InetSocketAddress("10.108.164.66", 30001));//��������
		cf3.awaitUninterruptibly();//�ȴ����Ӵ������ 
		*/
//		NioDatagramConnector connector = MinaUtil.createDatagramConnector();
		//	ConnectFuture cf = connector.connect(new InetSocketAddress(mgr.localAddr, mgr.uPort));//��������
		
	//	NioDatagramConnector connector = MinaUtil.CreatBoardcast();
	//	ConnectFuture cf = connector.connect(new InetSocketAddress("255.255.255.255", 9123));	
		for(int r=0;r<1;r++){
			
		
		for(int j=0;j<30;j++){
			System.out.println("---------------------------��"+(j+1)+"�η���----------------------");
          for(int i=0; i<msgList.size(); i++){
        	  cf.getSession().write(msgList.get(i));
		  }
		try {		
				Thread.sleep(2000);			
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		
		}
		
		try {		
			Thread.sleep(5000);			
	    } catch (InterruptedException e) {			
		e.printStackTrace();
	}
	}
	}
	


	//TCPͨ��
	public static void TCPSessionOpened(IoSession session){

	}
	
	
	public static void TCPSessionCreated(IoSession session){
		log.info("�¿ͻ�������");
	     log.info("session" + session.toString() +  
	       		 "###" + "create time:" + System.currentTimeMillis());
	     
		 MinaUtil.inTCPTotalCount();
		 MinaUtil.iniSessionReferance(session); 
	}
	
	public static void TCPMessageReceived(IoSession session, Object message){
		
		if(message.toString().equals("5First")){
		}
		
		if(message instanceof WsnMsg){
    		WsnMsg msg = (WsnMsg)message;
    	}else{

    	}
	}
	

	private static int currentAccount = 0;
	static long t1 = 0;
	static long t2 = 0;
	
	public static void TCPMessageSent(IoSession session, Object message) {

    }
	
	
	public static void TCPMessageIdle(IoSession session, IdleStatus status){
 
	}
	
    public static void TCPSessionClosed(IoSession session) {
        System.out.println("one Clinet Disconnect !");       
       
  		//Դ��
  		MinaUtil.deTCPTotalCount();
        //�ӱ����ͨ����ɾ��
        MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
        log.info("sessionClosed:" + session.toString());
        
        ConcurrentHashMap<String, IoSession> map = MsgQueueMgr.getDest_session();
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
        	String key = (String) it.next();
        	IoSession se = map.get(key);
        	log.info("key:" + key);
        	log.info("value:" + session.toString());
        }
        //�ر�����
        session.getService().dispose();
    }
    
    
    
	//udpͨ��
	public static void UDPSessionOpened(IoSession session){
		
	}
	
	public static void UDPSessionCreated(IoSession session){
	//	session.write("send message...");
        MsgInsert mi = MsgGenerator.createMsgInsert();        
        session.write(mi);
        
        System.out.println("�¿ͻ�������");
        MinaUtil.inUDPTotalCount();
        MinaUtil.iniSessionReferance(session);
	}
	
	public static void UDPMessageReceived(IoSession session, Object message){
		if(message instanceof WsnMsg){
    		WsnMsg msg = (WsnMsg)message;
    		RtMgr.getInstance().getState().processMsg(session, msg);
    	}else
    		System.out.println("receive message:" + message.toString());
	}
	
	
	public static void UDPMessageIdle(IoSession session, IdleStatus status){
        //System.out.println("���ӿ���");    
        System.out.println(new Date(System.currentTimeMillis()).toString());
    //    session.close(true);  //close right now���ر�ͨ��
	}
	
    public static void UDPSessionClosed(IoSession session) {
        System.out.println("one Clinet Disconnect !");
        MinaUtil.deUDPTotalCount();
        MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
        //�ر�����
        session.getService().dispose();
    }
    static int t = 0;
    public static void UDPMessageSent(IoSession session, Object message) {
    
    }
}
