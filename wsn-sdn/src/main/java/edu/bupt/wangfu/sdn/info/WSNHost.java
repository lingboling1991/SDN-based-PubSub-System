package edu.bupt.wangfu.sdn.info;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-5.
 */
public class WSNHost extends DevInfo {
	public String ip;
	private Map<String, List<String>> subers = new ConcurrentHashMap<String, List<String>>();

}
