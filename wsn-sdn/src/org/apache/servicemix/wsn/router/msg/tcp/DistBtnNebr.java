package org.apache.servicemix.wsn.router.msg.tcp;

import java.io.Serializable;

public class DistBtnNebr implements Serializable {
	public int dist; // ����ھӵľ���
	
	public DistBtnNebr(int dist) {
		this.dist = dist;
	}
	
	public int getDist() {
		return dist;
	}
}
