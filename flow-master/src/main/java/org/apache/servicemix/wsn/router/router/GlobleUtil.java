package org.apache.servicemix.wsn.router.router;

import edu.bupt.wangfu.sdn.floodlight.RestProcess;
import edu.bupt.wangfu.sdn.info.*;
import edu.bupt.wangfu.sdn.queue.QueueManagerment;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by root on 15-10-6.
 */
public class GlobleUtil {
    private static GlobleUtil INSTANCE;
    public Map<String, Controller> controllers = new ConcurrentHashMap<String, Controller>();
    public static List<Flow> initFlows = new ArrayList<Flow>();
    public static String REST_URL = "http://10.108.166.220:8080";//nll
    private static int index = 2;//nll
    private static Timer timer = new Timer();

    private GlobleUtil(){
        //init static initFlows{queueFlow, topics}
        Flow flow = new Flow("queue");
        initFlows.add(flow);

        // start timer to recaclateRoute
        timer.schedule(new GlobalTimerTask(), 2000, 5 * 60 * 1000);
    };

    public void init(){
        //get realtime global info
        reflashGlobleInfo();

        //init all switchs
        for(Map.Entry<String, Controller> entry: controllers.entrySet()){
            Controller controller = entry.getValue();
            initSwitchs(controller);
        }


    }
    public boolean initSwitchs(Controller controller){
        boolean success = false;

        //down init flows
        downFlow(controller, initFlows);

        return success;
    }
    public boolean reflashGlobleInfo(){

        //Traversal controllers, GET global realtime status
        for(Map.Entry<String, Controller> entry: controllers.entrySet()){
            Controller controller = entry.getValue();
            if(!controller.isAlive()){
                controllers.remove(entry.getKey());
                continue;
            }
            controller.reflashSwitchMap();
        }
        return true;
    }

    public static Map<String, Switch> getRealtimeSwitchs(Controller controller){//nll

        Map<String, Switch> switches = new HashMap<String, Switch>();
        try{
            String url = REST_URL+"/wm/core/controller/switches/json";
            String body = doClientGet(url);
            JSONArray json = new JSONArray(body);
            System.out.println(body);
            System.out.println(json.length());
            for(int i=0;i<json.length();i++){//��ȡ����������
                Switch swc = new Switch();
                String DPID = json.getJSONObject(i).getString("switchDPID");
                swc.setDPID(DPID);
                String mac = null;
                Map<Integer, DevInfo> wsnHostMap = new ConcurrentHashMap<Integer, DevInfo>();//�����������ӵ������豸
                String url_af = REST_URL+"/wm/core/switch/all/features/json";
                String body_af = doClientGet(url_af);
                JSONArray json_af = new JSONArray(body_af);
                System.out.println(body_af);
                JSONArray json_each = json_af.getJSONObject(0).getJSONArray(DPID);//���ݵ�ǰ��DPID��ȡ������������������Ϣ
                JSONArray json_port = json_each.getJSONObject(0).getJSONArray("portDesc");
                for(int j = 0;j<json_port.length();j++){
                    if(json_port.getJSONObject(j).getString("portNumber").equals("local")){
                        mac = json_port.getJSONObject(j).getString("hardwareAddress");
                        swc.setMac(mac);
                    }else{
                        if(!json_port.getJSONObject(j).getString("portNumber").equals("1")){//����˿ںŲ���local��1��Ϊ�����ӵ��豸
                            int portNum = json_port.getJSONObject(j).getInt("portNumber");
                            String macAddr = json_port.getJSONObject(j).getString("hardwareAddress");
                            boolean flag = false;//������¼�Ƿ���map���ҵ��ý�����
                            Switch swth = null;
                            for(Map.Entry<String,Switch> entry : switches.entrySet()){
                                Switch swt = entry.getValue();
                                if(swt.getMac().equals(macAddr)){
                                    flag = true;
                                    swth = swt;
                                }
                            }
                            if(flag){
                                wsnHostMap.put(portNum,swth);
                            }else{
                                WSNHost host = new WSNHost();
                                host.setMac(macAddr);
                                wsnHostMap.put(portNum,host);
                            }
                            swc.setWsnHostMap(wsnHostMap);
                        }
                    }
                }
                switches.put(DPID,swc);
            }
            return switches;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return switches;
    }
    private static String doClientGet(String url) {//nll
        try {
            HttpClient httpclient = new HttpClient();
            GetMethod getMethod= new GetMethod(url);
//				 PostMethod postMethod = new PostMethod(url);
//				if (postData != null) {
//					getMethod.addParameters(postData);
//				}
            httpclient.executeMethod(getMethod);
            String body = getMethod.getResponseBodyAsString();
            getMethod.releaseConnection();
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void addController(String controllerAddr){

        Controller newController = new Controller(controllerAddr);

        newController.reflashSwitchMap();

        controllers.put(controllerAddr, newController);

    }

    public static boolean downFlow(String url, JSONObject content){
        boolean success = false;
        return RestProcess.doClientPost(url, content).get(0).equals("200");

    }

    public static boolean downFlow(Controller controller, List<Flow> flows){
        boolean success = false;
        for(Flow flow: flows){
            if(downFlow(controller, flow)) success = true;
        }
        return true;
    }

    public static boolean downFlow(Controller controller, Flow flow){
        boolean success = false;
        return RestProcess.doClientPost(controller.url, flow.getContent()).get(0).equals("200");

    }

    public static GlobleUtil getInstance(){
        if(INSTANCE == null) INSTANCE = new GlobleUtil();
        return INSTANCE;
    }

    class GlobalTimerTask extends TimerTask{

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {

            //whether to adjust queue
            QueueManagerment.qosStart();

            //whether to adjust route

        }
    }
}
