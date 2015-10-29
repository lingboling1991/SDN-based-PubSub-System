package org.apache.servicemix.wsn.router.router;

import org.apache.servicemix.wsn.router.mgr.base.MsgSubsForm;
import org.apache.servicemix.wsn.router.mgr.base.SysInfo;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.TargetGroup;
import org.apache.servicemix.wsn.router.wsnPolicy.msgs.WsnPolicyMsg;

import java.util.ArrayList;

/**
 * 计算指定名称的转发路由
 * 针对该名称的所有订阅节点，由集群名比较选取根节点
 * 根据迪杰斯特拉算法由根节点开始计算转发树
 * 将转发树的根节点及本节点的吓一跳转发记录在名称路由结构中
 *
 * @author Sylvia
 */

public class Router extends SysInfo implements IRouter {

	public void route(String topic) {
		if (topic == null || topic.length() == 0)
			return;
		ArrayList<String> goal = new ArrayList<String>();

		// 将所有订阅此名称的集群加入goal中
		String[] splited = topic.split(":");
		MsgSubsForm msf = groupTableRoot;

		// 添加goal中的订阅集群，并将指针指向与此标题相对应的路由树模块
		for (int i = 0; i < splited.length; i++) {
			if (msf.topicChildList.containsKey(splited[i])) {
				msf = msf.topicChildList.get(splited[i]);
			} else
				return;
		}
		//将所有其他集群的代表地址加入到goal中
		for (String name : msf.subs)
			goal.add(name);
		String ex = "";
		// 删除goal中有限制的集群
		if (!goal.isEmpty()) {
			for (int i = 0; i < splited.length; i++) {
				if (i > 0)
					ex += ":";
				ex += splited[i];
				WsnPolicyMsg wpm = ShorenUtils.decodePolicyMsg(ex);
				if (wpm != null) {
					for (TargetGroup tg : wpm.getAllGroups()) {
						if (goal.contains(tg.getName()) && tg.isAllMsg()) {
							goal.remove(tg.getName());
							System.out.println("group " + tg.getName() + " is rejected by " + ex);
							if (goal.isEmpty()) {
								break;
							}
						}
					}
				}
			}
		}

		// 清空原路由路径'
		msf.routeNext.clear();
		msf.routeRoot = "";
		routeTopic(goal, msf, topic);
	}

	protected void routeTopic(ArrayList<String> goal, MsgSubsForm msf, String topic) {
		if (!goal.isEmpty()) {

			// 计算迪杰斯特拉路径
			Dijkstra dij = new Dijkstra();
			Node start = dij.init(lsdb, goal, groupName);
			if (!start.getName().equals(groupName)) {
				if (groupMap.containsKey(start.getName())) {
					msf.routeRoot = start.getName();
				}
			} else
				msf.routeRoot = "";

			System.out.println("calculate " + topic + " root: " + start.getName());
			// 若此集群也订阅了此信息，则计算其孩子
			if (goal.contains(groupName) && goal.size() > 1) {
				dij.computePath(start);
				if (!start.getName().equals(groupName) && dij.CalFather().trim().length() == 0) {
					goal.remove(start.getName());
					routeTopic(goal, msf, topic);
				}
				System.out.print("next stop: ");
				for (String next : dij.savePath()) {
					if (next != null) {
						if (groupMap.containsKey(next)) {
							msf.routeNext.add(next);
							System.out.print(next + "  " + groupMap.get(next).addr + "  ");
						} else {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							route(topic);
						}
					}
				}
				System.out.println(" ");
			} else {
				msf.routeNext.clear();
			}
		}
	}
}