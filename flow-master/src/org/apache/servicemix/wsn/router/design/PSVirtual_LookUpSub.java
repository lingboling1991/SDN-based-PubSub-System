package org.apache.servicemix.wsn.router.design;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupSubscriptions;
import org.apache.servicemix.wsn.router.msg.tcp.MsgLookupGroupSubscriptions_;


public class PSVirtual_LookUpSub {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ObjectInputStream ois;
		ObjectOutputStream oos;
		try {
			System.out.println("���ڼ���");
			ServerSocket PSServer=new ServerSocket(2000);
			Socket Server=PSServer.accept();
			System.out.println("���ӽ���");
			
			ois=new ObjectInputStream(Server.getInputStream());
			oos=new ObjectOutputStream(Server.getOutputStream());
			
			MsgLookupGroupSubscriptions require=(MsgLookupGroupSubscriptions) ois.readObject();
			System.out.println("����������");
			MsgLookupGroupSubscriptions_ rtnMember = new MsgLookupGroupSubscriptions_();

		    rtnMember.topics.add("�¶�");
		    rtnMember.topics.add("ʪ��");
			
		    oos.writeObject(rtnMember);
		    System.out.println("����������");
		    oos.close();
	        ois.close();
	        Server.close();
	        PSServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

}
