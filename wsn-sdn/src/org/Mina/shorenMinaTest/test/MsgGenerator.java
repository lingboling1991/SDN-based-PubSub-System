package org.Mina.shorenMinaTest.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.Mina.shorenMinaTest.MinaUtil;
import org.Mina.shorenMinaTest.msg.tcp.*;
import org.Mina.shorenMinaTest.msg.WsnMsg;

/**
 *
 */
public class MsgGenerator {
	


	
	public static int tPort = 30001;
	public static int uPort = 30002;

	public static EmergencyMsg createEmergencyMsg(WsnMsg msg){
		EmergencyMsg emsg = new EmergencyMsg(msg);
		
		return emsg;
	}
	
	public static MsgNotis createMsgNotis(){
		MsgNotis mn = new MsgNotis();
		mn.doc = "this is a notice message.";  //֪ͨ����
		mn.originatorAddr = "broke ip";   //�ṩ֪ͨ��broker��IP��ַ
		mn.originatorGroup = "broke group";  //�ṩ֪ͨ��broker���ڼ�Ⱥ����
		mn.sender = "sender";  //ת���ߵ���Ϣ
		mn.topicName = "testTopicName";  //֪ͨ����
		mn.sendDate = new Date(System.currentTimeMillis()).toString(); //��Ϣ������ʱ��

		System.out.println(mn.sendDate.toString());   //Tue Jun 18 16:41:54 CST 2013
		return mn;
	}
	
	public static MsgInfoChange createMsgInfoChange(){
		MsgInfoChange mic = new MsgInfoChange();
		mic.addr = "addr";
		mic.originator = "orig";
		mic.port = 2222;
		mic.sender = "mic sender";
		
		return mic;
	}
	
	public static MsgSetAddr createMsgSetAddr(){
		MsgSetAddr ms = new MsgSetAddr();
		ms.addr = "addr";
		ms.port = 1233;
		
		return ms;
	}
	
	public static MsgAdminChange createMsgAdminChange(){
		MsgAdminChange mac = new MsgAdminChange();
		mac.NewAdminAddr = "new admin";
		
		return mac;
	}
	
	public static MsgInsert createMsgInsert(){		
		MsgInsert ms = new MsgInsert();
		ms.tagetGroupName = "this group";
		ms.name = "there is msg inserted";
		ms.addr = "localAddress";
		ms.id = 123456789;
		ms.tPort = 10243;
		ms.uPort = 10244;
		
		return ms;
	}
	
	//���ɶ��ָ����Ϣ
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<WsnMsg> generateMsgs(){
		List<WsnMsg> msgList = new ArrayList<WsnMsg>();
		MsgNotis mn = MsgGenerator.createMsgNotis();  
		
        MsgInfoChange mic = MsgGenerator.createMsgInfoChange();    
        MsgSetAddr msa = MsgGenerator.createMsgSetAddr();            
        
        MsgAdminChange mac = MsgGenerator.createMsgAdminChange();   
        MsgInsert mi = MsgGenerator.createMsgInsert();       
        
        EmergencyMsg em = MsgGenerator.createEmergencyMsg(mi);
        for(int i=0; i<MinaUtil.testMax; i++){
        	msgList.add(em);
//             msgList.add(mn);
//             msgList.add(mic);
//             msgList.add(mac);
        }
        return msgList;
	} 
	
	public static void main(String[] args) {
		createMsgNotis();

	}

}
