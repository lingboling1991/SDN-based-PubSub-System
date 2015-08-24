package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

public class PolicyDB implements Serializable {

	/**
	 * ������Ϣ��
	 */
	private static final long serialVersionUID = 1L;
	public long time;
	public boolean clearAll; //�Ƿ�Ϊȫ�����
	public ArrayList<WsnPolicyMsg> pdb;
	public  HashMap<String, GroupUnit> groupMsg;
	
	public PolicyDB () {
		pdb = new ArrayList<WsnPolicyMsg>();
		groupMsg = new HashMap<String, GroupUnit>();
	}
}