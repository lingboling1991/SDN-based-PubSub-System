package edu.bupt.wangfu.sdn.queue;

//import com.sdn.OvsOperation.OvsOperate;

import edu.bupt.wangfu.sdn.info.Controller;
import edu.bupt.wangfu.sdn.info.DevInfo;
import edu.bupt.wangfu.sdn.info.Switch;
import edu.bupt.wangfu.sdn.sflow.SflowAPI;
import org.apache.servicemix.wsn.router.router.GlobleUtil;

import java.util.Map;

/**
 * Created by root on 15-7-9.
 */
public class QueueAdjust extends Thread {

	public static void main(String[] args) {

	}

	@Override
	public void run() {

		for (Map.Entry<String, Controller> entry : GlobleUtil.getInstance().controllers.entrySet()) {
			adjustController(entry.getValue());
		}


	}

	private void adjustController(Controller controller) {
		Map<String, Switch> switchs = controller.getSwitchMap();
		for (Switch sw : switchs.values()) {
			Map<Integer, DevInfo> ports = sw.getWsnDevMap();
			for (Integer port : ports.keySet()) {
				double speed = SflowAPI.getSpeed(sw.getIpAddr(), port, ".ifinpkts");
				double bandWidth = ports.get(port).hashCode();
				if (speed > bandWidth / 2 && speed <= bandWidth * 2 / 3) {//weak
					QueueManagerment.enQueue(controller, sw.getDPID(), port, "3", "q3");
				} else if (speed > bandWidth * 2 / 3) {
					QueueManagerment.enQueue(controller, sw.getDPID(), port, "2", "q2");
					QueueManagerment.enQueue(controller, sw.getDPID(), port, "3", "q1");
				}else if (speed > bandWidth * 2 / 3) {
					QueueManagerment.enQueue(controller, sw.getDPID(), port, "2", "q2");
					QueueManagerment.enQueue(controller, sw.getDPID(), port, "3", "q1");
				}
			}
		}
	}

}
