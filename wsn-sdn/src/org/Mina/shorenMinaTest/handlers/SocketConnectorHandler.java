/**
 * @author shoren
 * @date 2013-6-5
 */
package org.Mina.shorenMinaTest.handlers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequestQueue;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.WsnMsg;
import org.Mina.shorenMinaTest.msg.tcp.MsgNotis;
import org.Mina.shorenMinaTest.queues.MsgQueueMgr;


/**
 *
 */
public class SocketConnectorHandler extends IoHandlerAdapter  {  
	  
	protected static  Logger logger  =  Logger.getLogger(SocketConnectorHandler.class );
	public static boolean check = true;
	public static String TcpExceptionSessionIP = null;
      
	    // �����Ϸ������󴥷��˷���.
	    public void sessionCreated(IoSession session) {
	    	MinaUtil.iniSessionReferance(session);
	    }
	 
	    // ���������ʱ 
	    @Override
	    public void sessionOpened(IoSession session) throws Exception {

	    }
	 
	    // ���յ���Ϣʱ
	    @Override
	    public void messageReceived(IoSession session, Object message) throws Exception{
	    	
	    	if(message instanceof WsnMsg){
	    		WsnMsg msg = (WsnMsg)message;
	    	}else
	    		System.out.println("TCP receive message:" + message.toString());
	    	
	    }
	 
	    // ����Ϣ�Ѿ����͸��������󴥷��˷���. || ÿ����һ����Ϣ�����
	    @Override
	    public void messageSent(IoSession session, Object message) {

	         int length = 0;  
	         WriteRequestQueue rqueue = session.getWriteRequestQueue(); ///д�������

	         length = rqueue.size()/2;
	         session.setAttribute("qLength", length); 
	         if(length > MinaUtil.maxThree)
	        	 MinaUtil.maxThree = length;
	         
	         if(length <= MinaUtil.getSingleminth()){  
	         	if(!((String)session.getAttribute("state")).equals(MinaUtil.SHealthy)){ 
	         		MinaUtil.deTCPBlockCount();

	         			String last_state = (String)session.getAttribute("state");
	         			session.setAttribute("state", MinaUtil.SHealthy);
	                 	session.setAttribute("last_state", last_state);
	                 	
	                 	session.setAttribute("lossCount", 0);
	         	}         	
	         	
	         }else if(length > MinaUtil.getSingleminth() && length < MinaUtil.getSinglemaxth()){
	        	 String last_state = (String)session.getAttribute("state"); 
	          	if(((String)session.getAttribute("state")).equals(MinaUtil.SHealthy)){         		
	          			MinaUtil.inTCPBlockCount();
	          			}
	          	session.setAttribute("state", MinaUtil.SSick);
	          	session.setAttribute("last_state", last_state);     	
	          //���㶪���ʣ�����          	 
	            int lossCount = MinaUtil.calPacket_loss_rate(session);
	            session.setAttribute("lossCount", lossCount);
	          	
	         }else if(length >= MinaUtil.getSinglemaxth()){  
	        	 //����UDP������ȫ��״̬���ж��Ƿ�ر��߳�
	        	 String last_state = (String)session.getAttribute("state");  
	        	
	          	if(((String)session.getAttribute("state")).equals(MinaUtil.SHealthy)){         		
	           			MinaUtil.inTCPBlockCount();	
	           	}
	           	session.setAttribute("state", MinaUtil.SSick);
	           	session.setAttribute("last_state", last_state);
	         }
	         
	        // MsgNotis mn = (MsgNotis) message;
	         //System.out.println("���͵���ϢΪ:   "+mn.doc);
	       //  System.out.println("MessageSent!");
	    }
	 
	    // �ر�ʱ
	    @Override
	    public void sessionClosed(IoSession session) {
	  		//Դ��
	  		MinaUtil.deTCPTotalCount();
	        //�ӱ����ͨ����ɾ��
	        MsgQueueMgr.getDest_session().remove(session.getAttribute("addr"));
	        System.out.println("TCPsessionClosed:" + session.toString());
	        
	        ConcurrentHashMap<String, IoSession> map = MsgQueueMgr.getDest_session();
	        Iterator it = map.keySet().iterator();
	        while(it.hasNext()){
	        	String key = (String) it.next();
	        	IoSession se = map.get(key);
	        }
	        //�ر�����
	        session.getService().dispose();
	    }
	 
	    // �����ӿ���ʱ�����˷���.
	    @Override
	    public void sessionIdle(IoSession session, IdleStatus status) {
	    	session.close(true);  //close right now���ر�ͨ��
	    }
	       
	 
	    // ���ӿ������������׳��쳣δ������ʱ�����˷���
	    public void exceptionCaught(IoSession session, Throwable cause) {
	    	System.out.println("���������׳��쳣"+session.toString()+cause.toString());

	        session.close(true);  //close right now���ر�ͨ��
	        return;
	    }
	    
	    
	    public static String getTcpExceptionSession(){
	    	if(TcpExceptionSessionIP != null)
	    	    return TcpExceptionSessionIP;
	    	else
	    		return null;
	    }
}
