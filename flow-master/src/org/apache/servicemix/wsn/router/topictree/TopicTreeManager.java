/*	
 * author:goucanran date:2013-6-20
 * 主题树管理界面及相关逻辑
 * 
 * */
package org.apache.servicemix.wsn.router.topictree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.naming.NamingException;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jaxe.Jaxe;

import org.apache.servicemix.application.WSNTopicObject;
import org.apache.servicemix.wsn.router.admin.AdminMgr;
import org.apache.servicemix.wsn.router.design.Data;
import org.apache.servicemix.wsn.router.design.PSManagerUI;
import org.apache.servicemix.wsn.router.msg.tcp.UpdateTree;
import org.apache.servicemix.wsn.router.wsnPolicy.ShorenUtils;
import org.apache.servicemix.wsn.router.wsnPolicy.WsnPolicyInterface;
import org.w3c.dom.Document;

import com.bupt.wangfu.ldap.*;

public class TopicTreeManager {
	private JPanel TTFrame = null;
	private JMenuBar TTMenuBar = null;
	private JToolBar TTToolBar = null;
	private JTree TTTree = null;
	private JTree LibTree = null;
	public static JTree LibTreeForSchema = null;
	private DefaultMutableTreeNode root = null; 
	private DefaultTreeModel model = null;
	private JPopupMenu popMenu= null;
	private static Toolkit kit;
	private static Dimension screenSize;
	public WsnPolicyInterface wpi = null;
	public Data data = null;
	private HashMap<String,Integer> topicmap = new HashMap<String,Integer>();
	private int topic_counter;
	private JPanel kuAndTree;
	public Ldap lu = null;
	public DefaultMutableTreeNode lib_root = new DefaultMutableTreeNode("root");
	DefaultTreeModel libmodel = new DefaultTreeModel(lib_root);
	JFrame editingJFrame = new JFrame("重命名");
	JFrame addJFrame = new JFrame("添加主题");
	JFrame newTreeFrame = new JFrame("新建主题树");
	ImageIcon addimage = new ImageIcon("./icon/add.jpg");
	ImageIcon deleteimage = new ImageIcon("./icon/delete.jpg");
	ImageIcon modifyimage = new ImageIcon("./icon/modify.jpg");
	ImageIcon newtreeimage = new ImageIcon("./icon/newtree.png");
	ImageIcon saveimage = new ImageIcon("./icon/save.jpg");
	ImageIcon reflashimage = new ImageIcon("./icon/reflash.png");
	ImageIcon treeopenimage = new ImageIcon("./icon/图标2.jpg");
	ImageIcon treecloseimage = new ImageIcon("./icon/图标.jpg");
	ImageIcon leafimage = new ImageIcon("./icon/composite.jpg");
	ImageIcon strategyImage = new ImageIcon("./icon/strategy.jpg");
	ImageIcon schemaimage = new ImageIcon("./icon/schema.png");
	private int counter = 0;
	private PSManagerUI ui;
	private TransformerFactory tFactory;
	private Transformer transformer;
	private DOMSource source;
	private StreamResult result;
	protected File schemaFile;
	public static WSNTopicObject topicTree;//为配合wsn向管理员索要全部主题树 构建的结构
	
	static
	{
		kit = Toolkit.getDefaultToolkit();  
        screenSize = kit.getScreenSize();
        
	}
	public TopicTreeManager(Data data,PSManagerUI ui){
		this.data = data;
		this.ui = ui;
		TTTree = new JTree(model);
		LibTree = new JTree(libmodel);
		LibTreeForSchema = new JTree(libmodel);
		LibTreeForSchema.setAutoscrolls(true);
		LibTree.setAutoscrolls(true);
	}
	public TopicTreeManager(Data data){
		this.data = data;
		TTTree = new JTree(model);
		LibTree = new JTree(libmodel);
		LibTreeForSchema = new JTree(libmodel);
		LibTreeForSchema.setAutoscrolls(true);

		LibTree.setAutoscrolls(true);
	}
	//总的初始化入口
	private void frame_init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException
	{
		TTFrame.setLayout(new BorderLayout());
		TTFrame.setSize(new Dimension(600, 520));
		TTFrame.setPreferredSize(new Dimension(600, 520));
		TTFrame.setOpaque(false);
		TTFrame.setBounds(screenSize.width/4 , screenSize.height/8, 
				1100 ,630);
	//	TTFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		TTFrame.setResizable(false);
		//String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		//UIManager.setLookAndFeel(lookAndFeel);
		
		tFactory = TransformerFactory.newInstance();
	    try {
			transformer = tFactory.newTransformer();
			source = new DOMSource();		    
//		    result = new StreamResult();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
		
		//连接LDAP数据库
		lu = new Ldap();
		lu.connectLdap(AdminMgr.ldapAddr,"cn=Manager,dc=wsn,dc=com","123456");
		
		OPFrame_init();//增加、修改主题的对话框初始化
		MenuBar_init();//菜单栏初始化
		ToolBar_init();//工具栏初始化
		PopMenu_init();//右键菜单初始化
		LibTree_init();//左侧树结构初始化
		Database_init();//LDAP数据初始化
		Tree_init();//右侧树结构初始化
		policy_init();//策略界面、逻辑初始化
	}
	public void Database_init() throws NamingException {
		// TODO Auto-generated method stub
		reload_LibTrees();
	}
	public void reload_LibTrees() throws NamingException {
		// TODO Auto-generated method stub
		
		TopicEntry libentry = new TopicEntry();
		libentry.setTopicName("all");
		libentry.setTopicCode("1");
		libentry.setTopicPath("ou=all_test,dc=wsn,dc=com");
		lib_root.setUserObject(libentry);
		
		lib_root.removeAllChildren();
		setTTTree(lib_root);		
		TTTreeToWSNObject();
		libmodel.reload(lib_root);
		LibTree.setModel(libmodel);
		LibTreeForSchema.setModel(libmodel);
		LibTreeForSchema.updateUI();

		LibTree.updateUI();
	}
	private void OPFrame_init() {
		//修改主题对话框初始化
		editingJFrame.setBounds(screenSize.width/2 , screenSize.height/2, 
				300 ,150);
		editingJFrame.setLayout(null);
		final JTextField editArea = new JTextField();
		editArea.setBounds(30, 20, 220, 30);
		editingJFrame.add(editArea);
		JButton editconfirm = new JButton("确定");
		editconfirm.setBounds(30, 70, 100,30);
		editconfirm.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 修改主题对话框确定后，刷新树
				JTree temptree = TTTree;
				DefaultMutableTreeNode treenode = (DefaultMutableTreeNode)TTTree.getLastSelectedPathComponent();
				if(treenode==null)
				{
					treenode = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
					temptree = LibTree;
				}
				String newname = editArea.getText().trim();
				
				if(treenode != null&&!newname.equals(new String("")))
				{
					TopicEntry tempentry = (TopicEntry)treenode.getUserObject();
				//	tempentry.setTopicName(newname);
					if(topicmap.containsKey(newname))
						tempentry.setTopicCode(topicmap.get(newname).toString());
					else
					{
						tempentry.setTopicCode(""+(++topic_counter));
						topicmap.put(newname, topic_counter);
					}
					
					String oldname = tempentry.getTopicName();
					UpdateTree ut = new UpdateTree(System.currentTimeMillis());
					ut.oldName = ShorenUtils.getFullName(tempentry);
					ut.newName = ut.oldName.replace(tempentry.getTopicName(), newname);
					ut.change = 0;
				 //   data.sendNotification(ut);
					try {
						lu.rename(tempentry, newname);
					} catch (NamingException e1) {
						e1.printStackTrace();
					}					

					treenode.setUserObject(tempentry);//改名后的节点加入treenode
					
					//遍历修改treenode字节点中的路径
					Queue<DefaultMutableTreeNode> queue = new LinkedList<DefaultMutableTreeNode>();
					queue.offer(treenode);
					while(!queue.isEmpty()){
						DefaultMutableTreeNode temptreenode = queue.poll();
						Enumeration list = temptreenode.children();
						while (list.hasMoreElements()){
							DefaultMutableTreeNode treenodeChild = (DefaultMutableTreeNode)list.nextElement();
							TopicEntry childUO = (TopicEntry)treenodeChild.getUserObject();
							childUO.setTopicPath(childUO.getTopicPath().replaceAll(oldname, newname));
							treenodeChild.setUserObject(childUO);
							queue.offer(treenodeChild);							
					}
					}	

					
					if(temptree == LibTree){
						libmodel.reload(lib_root);
						LibTree.setModel(libmodel);
						reload_TTTree(treenode);
						TTTreeToWSNObject();
					}
					else{
						model.reload(root);
						TTTree.setModel(model);
						try {
							reload_LibTrees();
						} catch (NamingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						search_LibTree(treenode,lib_root);
					}
					DefaultTreeModel tempmodel = (DefaultTreeModel)temptree.getModel();
					TreePath temp_path = new TreePath(tempmodel.getPathToRoot(treenode));
					temptree.setSelectionPath(temp_path);
					//TTTree.setSelectionPath(new TreePath(model.getPathToRoot(treenode)));

					
					data.sendNotification(ut);
				}
				editingJFrame.setVisible(false);
			}
		});
		JButton editcancel = new JButton("取消");
		editcancel.setBounds(150, 70, 100, 30);
		editcancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				editingJFrame.setVisible(false);
			}
		});
		editingJFrame.add(editconfirm);
		editingJFrame.add(editcancel);
		editingJFrame.setVisible(false);
		
		//添加主题对话框初始化
		addJFrame.setBounds(screenSize.width/2 , screenSize.height/2, 
				300 ,150);
		addJFrame.setLayout(null);
		final JTextField addArea = new JTextField();
		addArea.setBounds(30, 20, 220, 30);
		addJFrame.add(addArea);
		JButton addconfirm = new JButton("确定");
		addconfirm.setBounds(30, 70, 100,30);
		addconfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 添加主题对话框确定后，添加节点并且刷新
				JTree temptree = TTTree;
				DefaultMutableTreeNode temp_node = (DefaultMutableTreeNode)TTTree.getLastSelectedPathComponent();
				if(temp_node==null)
				{	
					temptree = LibTree;
					temp_node = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
				}
				TopicEntry temptopic = (TopicEntry)temp_node.getUserObject();
				TopicEntry newtopic = new TopicEntry();
				newtopic.setTopicName(addArea.getText());
				newtopic.setTopicCode(""+getTopicCode(newtopic.getTopicName()));
				newtopic.setTopicPath("ou="+newtopic.getTopicName()+","+temptopic.getTopicPath());
				lu.create(newtopic);
				
				DefaultMutableTreeNode new_node = new DefaultMutableTreeNode();
				new_node.setUserObject(newtopic);
				temp_node.add(new_node);
				
				//操作目标可能不同，调用相应函数刷新
				if(temptree == LibTree){
					libmodel.reload(lib_root);
					LibTree.setModel(libmodel);
					reload_TTTree(new_node);
					TTTreeToWSNObject();
				}
				else{
					model.reload(root);
					TTTree.setModel(model);
					try {
						reload_LibTrees();
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					search_LibTree(temp_node,lib_root);
				}
				DefaultTreeModel tempmodel = (DefaultTreeModel)temptree.getModel();
				TreePath temp_path = new TreePath(tempmodel.getPathToRoot(new_node));
				temptree.setSelectionPath(temp_path);
				addJFrame.setVisible(false);
				data.sendNotification(new UpdateTree(System.currentTimeMillis()));
				
				temptree.requestFocusInWindow();
				
				ui.reflashJtreeRoot();
			}
		});
		JButton addcancel = new JButton("取消");
		addcancel.setBounds(150, 70, 100, 30);
		addcancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addJFrame.setVisible(false);
			}
		});
		addJFrame.add(addconfirm);
		addJFrame.add(addcancel);
		addJFrame.setVisible(false);
		
		//新建领域操作，其实就是在在最上层建立一个节点
		newTreeFrame.setBounds(screenSize.width/2 , screenSize.height/2, 
				300 ,150);
		newTreeFrame.setLayout(null);
		final JTextField newtreeArea = new JTextField();
		newtreeArea.setBounds(30, 20, 220, 30);
		newTreeFrame.add(newtreeArea);
		JButton newconfirm = new JButton("确定");
		newconfirm.setBounds(30, 70, 100,30);
		newconfirm.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 点击新建树对话框确定按钮后，新建树
				if(!newtreeArea.getText().trim().equals(new String("")))
				{
					try {
						new_tree(newtreeArea.getText().trim());
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					newTreeFrame.setVisible(false);
				//	TTTree.requestFocusInWindow();
				}
				
				
				
			}
		});
		JButton newcancel = new JButton("取消");
		newcancel.setBounds(150, 70, 100, 30);
		newcancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				newTreeFrame.setVisible(false);
			}
		});
		newTreeFrame.add(newconfirm);
		newTreeFrame.add(newcancel);
		newTreeFrame.setVisible(false);
	}

	protected void search_LibTree(DefaultMutableTreeNode node,DefaultMutableTreeNode cur_node) {
		// 递归地寻找某节点并且选中
		TopicEntry tempentry = (TopicEntry)cur_node.getUserObject();
		TopicEntry targetentry = (TopicEntry)node.getUserObject();
		if(tempentry.getTopicPath().equals(targetentry.getTopicPath()))
			LibTree.setSelectionPath(new TreePath(libmodel.getPathToRoot(cur_node)));
		else
		{
			Enumeration<DefaultMutableTreeNode> children = cur_node.children();
			while(children.hasMoreElements()){
				DefaultMutableTreeNode child = children.nextElement();
				search_LibTree(node, child);
			}
		}
	}
	protected int getTopicCode(String topicName) {
		// 获取某主题对应的编码
		int index;
		if(!topicmap.containsKey(topicName))
		{	
			topicmap.put(topicName,++topic_counter);
			index = topic_counter;
		}
		else 
			index = topicmap.get(topicName);
		return index;
	}
	private void LibTree_init() {
		// 左侧主题树视图的初始化
		LibTree.setSize(250,500);
		
		LibTree.setRootVisible(false);
		LibTree.setEditable(false);
		LibTree.setDragEnabled(true);
		LibTree.setDropMode(DropMode.ON_OR_INSERT);
		LibTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		try {
			LibTree.setTransferHandler(new TreeTransferHandler1(lu,LibTree,TTTree,data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LibTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if(evt.getButton()==1)
				{
					//选中某主题树时，在右侧树中展开其子树
					TreePath path = LibTree.getPathForLocation(evt.getX(), evt.getY());
					if(path!=null)
					{
						LibTree.setSelectionPath(path);
						DefaultMutableTreeNode chosen_node = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
						topicmap.clear();
						topic_counter = 0;
						System.out.println("chosen_node"+chosen_node);
						TopicEntry tempentry = (TopicEntry)chosen_node.getUserObject();
						if(tempentry!=null&&!tempentry.getTopicCode().equals("null")){
							topicmap.put(tempentry.getTopicName(), Integer.parseInt(tempentry.getTopicCode()));
						root.removeAllChildren();
						root.setUserObject(tempentry);
						TTTree.setRootVisible(false);
						reload_TTTree(root);
						LibTree.setSelectionPath(path);
						new Thread(){

						void TTTreeToWSNObject(){
								TopicEntry rootNode = (TopicEntry) lib_root.getUserObject();
					    topicTree = new WSNTopicObject(rootNode, null); 
						Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
						queue .offer(topicTree);
						while(!queue.isEmpty()){
							WSNTopicObject to = queue.poll();
							List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
							if(!ls.isEmpty()){
								List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
								for(TopicEntry t : ls){
									WSNTopicObject wto = new WSNTopicObject(t, to);
									temp.add(wto);
									queue.offer(wto);
								}
								to.setChildrens(temp);
							}
						}}}.start();
					}else{
						JOptionPane.showMessageDialog( null, "查询选中主题失败，该主题可能已失效!");
						
					}
					}
				}
			}
		});
		LibTreeForSchema.setRootVisible(false);
		LibTreeForSchema.setEditable(false);
		LibTreeForSchema.setDragEnabled(false);
		LibTreeForSchema.setDropMode(DropMode.ON_OR_INSERT);
		LibTreeForSchema.setToggleClickCount(2);
//		LibTreeForSchema.e
		LibTreeForSchema.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		try {
			LibTreeForSchema.setTransferHandler(new TreeTransferHandler1(lu,LibTree,TTTree,data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LibTreeForSchema.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){//点击几次，这里是双击事件
					
					TreePath path = LibTreeForSchema.getPathForLocation(e.getX(), e.getY());
					LibTreeForSchema.setSelectionPath(path);
					DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTreeForSchema.getLastSelectedPathComponent();
					if(path!=null&&chosenSchema.isLeaf())
					{						
					//选中某主题树时，在右侧树中展开其子树);
					File clickSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
					if(clickSchemaFile.exists()){
					Jaxe.schemaFrame.ouvrir(clickSchemaFile);
					ui.lblNewLabel_5.setText("        当前：" + chosenSchema.toString());
					ui.lblNewLabel_5.setName(chosenSchema.toString());
					}else
						JOptionPane.showMessageDialog(null, "没有该主题的schema： "+chosenSchema.toString());
					}
					
				}
				
			}
			
			public void mousePressed(MouseEvent evt) {
				if(evt.getButton()==1)
				{
//					TreePath path = LibTreeForSchema.getPathForLocation(evt.getX(), evt.getY());
//					if(path!=null)
//					{
//						LibTreeForSchema.setSelectionPath(path);
//					DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTreeForSchema.getLastSelectedPathComponent();
//					//选中某主题树时，在右侧树中展开其子树);
//					File clickSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
//					if(clickSchemaFile.exists()){
//					Jaxe.schemaFrame.ouvrir(clickSchemaFile);
//					ui.lblNewLabel_5.setText("        当前：" + chosenSchema.toString());
//					ui.lblNewLabel_5.setName(chosenSchema.toString());
//					}else
//						JOptionPane.showMessageDialog(null, "该主题不存在schema！");
//					}
				}}});
					
		
		LibTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if(evt.getButton()==3)
				{
					//首先刷新右侧树，主要用以解决直接在左侧右键配置策略时，防止上次在右侧树的选择影像此次的左侧直接右键操作
					TreePath path = LibTree.getPathForLocation(evt.getX(), evt.getY());
					if(path!=null)
					{
						LibTree.setSelectionPath(path);
						DefaultMutableTreeNode chosen_node = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
						topicmap.clear();
						topic_counter = 0;
						System.out.println("chosen_node"+chosen_node);
						TopicEntry tempentry = (TopicEntry)chosen_node.getUserObject();
						if(tempentry!=null&&!tempentry.getTopicCode().equals("null")){
							topicmap.put(tempentry.getTopicName(), Integer.parseInt(tempentry.getTopicCode()));
						root.removeAllChildren();
						root.setUserObject(tempentry);
						TTTree.setRootVisible(false);
						reload_TTTree(root);
						LibTree.setSelectionPath(path);
						new Thread(){

						void TTTreeToWSNObject(){
								TopicEntry rootNode = (TopicEntry) lib_root.getUserObject();
					    topicTree = new WSNTopicObject(rootNode, null); 
						Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
						queue .offer(topicTree);
						while(!queue.isEmpty()){
							WSNTopicObject to = queue.poll();
							List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
							if(!ls.isEmpty()){
								List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
								for(TopicEntry t : ls){
									WSNTopicObject wto = new WSNTopicObject(t, to);
									temp.add(wto);
									queue.offer(wto);
								}
								to.setChildrens(temp);
							}
						}}}.start();
					}else{
						JOptionPane.showMessageDialog( null, "查询选中主题失败，该主题可能已失效!");
						
					}
					}
					//右键时弹出右键菜单
					TreePath path2 = LibTree.getPathForLocation(evt.getX(), evt.getY());
					LibTree.setSelectionPath(path2);
					popMenu.show(LibTree, evt.getX(), evt.getY());
				}
			}
		});
		JScrollPane TreePane = new JScrollPane(LibTree);
		TreePane.setBounds(30, 30, 300, 500);
		TreePane.setPreferredSize(new Dimension(150,500));
		Border title1 = BorderFactory.createTitledBorder("库");
		TreePane.setBorder(title1);
		TreePane.setOpaque(false);
		TTFrame.add(BorderLayout.WEST, TreePane);
	}
	
//	 public JTree getLibTree(){
//		 JTree libTreeCopy = LibTree;
//		return libTreeCopy;
//	}
	
	//从Jtree解析到WSNTopicIObject
	public void TTTreeToWSNObject(){
		 TopicEntry rootNode = (TopicEntry) this.lib_root.getUserObject();
		    topicTree = new WSNTopicObject(rootNode, null); 
			Queue<WSNTopicObject> queue = new LinkedList<WSNTopicObject>();
			queue .offer(topicTree);
			while(!queue.isEmpty()){
				WSNTopicObject to = queue.poll();
				List<TopicEntry> ls = lu.getSubLevel(to.getTopicentry());
				if(!ls.isEmpty()){
					List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
					for(TopicEntry t : ls){
						WSNTopicObject wto = new WSNTopicObject(t, to);
						temp.add(wto);
						queue.offer(wto);
						//读取schema，并写入文件
						
						Document schemaT = t.getSchema();
						if(schemaT!=null){
						try {
							source.setNode(schemaT);	    
							result = new StreamResult(new File("schema/"+t.getTopicName()+".xsd"));
//							result.setSystemId(new File("schema/"+t.getTopicName()+".xsd"));;						    
						    transformer.transform(source, result);
						    
						   } catch (Exception ex) {						    
						    ex.printStackTrace();
						   }
					//	System.out.println(t.getTopicName());
						}
					}
					to.setChildrens(temp);
				}
			}
	}
	
	protected void setTTTree(DefaultMutableTreeNode tempnode) {
		// 递归构造右侧的新树
	
		List<TopicEntry> topics = null;
		TopicEntry tempentry = (TopicEntry) tempnode.getUserObject();		
			
			//if(tempentry.getTopicCode()!=null){
			topics = lu.getSubLevel(tempentry);
//			List<WSNTopicObject> temp = new ArrayList<WSNTopicObject>();
			for(TopicEntry topic : topics){
				DefaultMutableTreeNode newnode = new DefaultMutableTreeNode();
				int tempTopicCode = 0 ;
				if(!tempentry.getTopicCode().equals("null")){
				 tempTopicCode = Integer.parseInt(tempentry.getTopicCode());}
				topicmap.put(topic.getTopicName(), tempTopicCode);
				if(tempTopicCode>topic_counter)
					topic_counter = tempTopicCode;
				newnode.setUserObject(topic);							
				setTTTree(newnode);
				
				tempnode.add(newnode);
				
				
		}
		//	}
	}
	private void policy_init() {
		// 策略模块初始化
		JPanel wpiPanel = new JPanel();
		wpi = new WsnPolicyInterface(wpiPanel,lu);
		wpiPanel.setBounds(690, 30, 400, 520);
		//TTFrame.add(BorderLayout.EAST, wpiPanel);
	}
	private void PopMenu_init() {
		// 右键菜单初始化
		popMenu = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("添加",addimage);
		addItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				add_node_to_tree();
			}
		});
	 	JMenuItem delItem = new JMenuItem("删除",deleteimage);
	 	delItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				delete_node_from_tree();
			}
		});
	 	JMenuItem editItem = new JMenuItem("重命名",modifyimage);
	 	editItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				rename_node();
			}
		});
	 	
	 	JMenuItem strategyItem = new JMenuItem("配置策略",strategyImage);
	 	strategyItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				DefaultMutableTreeNode temp = (DefaultMutableTreeNode)TTTree.getLastSelectedPathComponent();
				JComboBox[] topicComboBox = {ui.comboBox, ui.comboBox_1,ui.comboBox_2,ui.comboBox_3,ui.comboBox_4,ui.comboBox_5};
				int rightLevel =0 ;
				if(temp != null) rightLevel = temp.getLevel();
				int leftLevel = ((DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent()).getLevel();
				int currentlevel =leftLevel+rightLevel;
				int[] indexs = new int[currentlevel];
				
				if(temp == null) {//右侧没有选择
					temp = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
					for(int j=0;j<leftLevel;j++){
						
						if(!temp.equals(lib_root)){
							int tempIndex = temp.getParent().getIndex(temp);
							indexs[leftLevel-1-j] = tempIndex+1;
							temp = (DefaultMutableTreeNode) temp.getParent();
							}
					}
				}else{	//右侧有选择 
				
				for(int i=0; i<rightLevel; i++){//首先处理右侧选择路径
					
					if(temp.getParent()!=null){
					int tempIndex = temp.getParent().getIndex(temp);
					indexs[currentlevel-1-i] = tempIndex+1;
					temp = (DefaultMutableTreeNode) temp.getParent();
					
					
				}
				}
				temp = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
				for(int j=0;j<leftLevel;j++){	//再处理左侧选择路径	
					
					if(!temp.equals(lib_root)){
						int tempIndex = temp.getParent().getIndex(temp);
						indexs[leftLevel-1-j] = tempIndex+1;
						temp = (DefaultMutableTreeNode) temp.getParent();
						}
				}
				}
					
				
				System.out.println("indexs:"+indexs+indexs[0]);
				for(int k=0; k<indexs.length;k++){
					
					topicComboBox[k].setSelectedIndex(indexs[k]);
				}
				
				
				
				Hashtable<Integer,DefaultMutableTreeNode> selectedTopicPath = new Hashtable<Integer,DefaultMutableTreeNode>();
				
					
					do{
						selectedTopicPath.put(new Integer(temp.getLevel()),temp);
						temp = (DefaultMutableTreeNode) temp.getParent();
					//	System.out.println("temp.getParent().toString()"+temp.getParent());
					}while(temp!=null&&!((DefaultMutableTreeNode)temp).equals(lib_root));//直到找到该主题树的根节点 即父节点为空
					if(temp!=null)
						System.out.println("选中主题的根节点不为空temp.toString()"+temp.toString());
					else {
						System.out.println("selectedTopicPath"+selectedTopicPath);//selectedTopicPath.add(lib_root);
						//循环遍历得到左侧树路径
						temp = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
						temp = (DefaultMutableTreeNode) temp.getParent();
						do{
							if(!selectedTopicPath.contains(temp)&&!temp.toString().equals("all"))
								selectedTopicPath.put(new Integer(temp.getLevel()),temp);
							temp = (DefaultMutableTreeNode) temp.getParent();
						//	System.out.println("temp.getParent().toString()"+temp.getParent());
						}while(temp!=null&&!((DefaultMutableTreeNode)temp).equals(lib_root));//直到找到该主题树的根节点 即父节点为空
						if(temp!=null)
							System.out.println("左侧库选中主题的根节点不为空temp.toString()"+temp.toString());
						System.out.println("遍历过左侧路径之后的selectedTopicPath"+selectedTopicPath);
					}
//					for(int i=0; i<selectedTopicPath.size();i++){
//						if(selectedTopicPath.get(i).getParent()==null){
//							topicComboBox[i].setSelectedIndex(lib_root.getIndex(selectedTopicPath.get(i)));
//						}else
//						topicComboBox[i].setSelectedIndex((selectedTopicPath.get(i).getParent().getIndex((selectedTopicPath.get(i)))));
//					//	topicComboBox[i].setSelectedItem(selectedTopicPath.get(i));
//						topicComboBox[i].repaint();
//					}
					ui.reflashFbdnGroups();
					ui.visualManagement.setSelectedIndex(2);//切换到策略配置界面
//					TopicEntry selectedTopic;
//					selectedTopic = (TopicEntry)temp.getUserObject();
//					String topicPath = selectedTopic.getTopicPath();
					
					
		//			wpi.updateTopics(null);
				}
			
		});
	 	
	 	JMenuItem schema = new JMenuItem("schema",schemaimage);
	 	schema.addActionListener(new ActionListener(){
//			private File schemaFile;

			@Override
			public void actionPerformed(ActionEvent e){
				
//				TreePath path = LibTreeForSchema.getPathForLocation(evt.getX(), evt.getY());
//				if(path!=null)
//				{
//					LibTreeForSchema.setSelectionPath(path);
				DefaultMutableTreeNode chosenSchema = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
				//选中某主题树时，在右侧树中展开其子树);
				File popSchemaFile = new File("schema/"+chosenSchema.toString()+".xsd");
				if(popSchemaFile.exists()){
					Jaxe.schemaFrame.ouvrir(popSchemaFile);
					LibTreeForSchema.setSelectionPath(LibTree.getLeadSelectionPath());
			
					ui.visualManagement.setSelectedIndex(3);//切换到策略配置界面
					//更新显示当前主题
					ui.lblNewLabel_5.setText("        当前：" + chosenSchema.toString());
					ui.lblNewLabel_5.setName(chosenSchema.toString());
				}else if(!chosenSchema.isLeaf()){
					LibTreeForSchema.setSelectionPath(LibTree.getLeadSelectionPath());
					ui.visualManagement.setSelectedIndex(3);//切换到策略配置界面
					//更新显示当前主题
					ui.lblNewLabel_5.setText("        当前：" + chosenSchema.toString());
					ui.lblNewLabel_5.setName(chosenSchema.toString());
				}else
					JOptionPane.showMessageDialog( null, "没有该主题的schema： "+chosenSchema.toString());
			}
		});
	 	
	 	popMenu.add(addItem);
	 	popMenu.add(delItem);
	 	popMenu.add(editItem);
	 	popMenu.add(strategyItem);
	 	popMenu.add(schema);
	}
	private void Tree_init() {
		// 右侧树显示区域初始化
		root = new DefaultMutableTreeNode("root");
		model = new DefaultTreeModel(root);
		TTTree.setModel(model);
		TTTree.setSize(440,500);
		TTTree.setRootVisible(false);
		TTTree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		//TTTree.putClientProperty("JTree.lineStyle", "Horizontal");
		//TTTree.setToggleClickCount(1);
		TTTree.setEditable(false);
		TTTree.setDragEnabled(true);
		TTTree.setDropMode(DropMode.ON_OR_INSERT);
		TTTree.setShowsRootHandles(false);
		
		DefaultTreeCellRenderer renderer = 
			new DefaultTreeCellRenderer();
		renderer.setClosedIcon(treecloseimage);
		renderer.setOpenIcon(treeopenimage);
		renderer.setLeafIcon(leafimage);
		TTTree.setCellRenderer(renderer);
		
		try {
			TTTree.setTransferHandler(new TreeTransferHandler1(lu,LibTree,TTTree,data));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TTTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if(evt.getButton()==3)
				{
					TreePath path = TTTree.getPathForLocation(evt.getX(), evt.getY());
					TTTree.setSelectionPath(path);
					popMenu.show(TTTree, evt.getX(), evt.getY());
				}
			}
		});
		
		JScrollPane TreePane = new JScrollPane(TTTree);
		TreePane.setBounds(250, 30, 440, 500);
		Border title1 = BorderFactory.createTitledBorder("主题树");
		TreePane.setBorder(title1);
		TreePane.setOpaque(false);
		TTFrame.add(BorderLayout.CENTER,TreePane);
	}
	private void MenuBar_init() {
		// 菜单栏初始化
		TTMenuBar = new JMenuBar();
		
		JMenu file = new JMenu("文件");
		
		JMenuItem open_menu = new JMenuItem("新建",newtreeimage);
		file.add(open_menu);
		
		JMenuItem save_menu = new JMenuItem("保存",saveimage);
		file.add(save_menu);
		
//		JMenuItem exit_menu = new JMenuItem("退出",closeimage);
//		exit_menu.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e){
//				TTFrame.setVisible(false);
//			}
//		});
//		file.add(exit_menu);
		
		JMenu tree = new JMenu("编辑树");
		
		JMenuItem add_menu = new JMenuItem("添加",addimage);
		add_menu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				add_node_to_tree();
			}
		});
		tree.add(add_menu);
		
		JMenuItem delete_menu = new JMenuItem("删除",deleteimage);
		delete_menu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				delete_node_from_tree();
			}
		});
		tree.add(delete_menu);
		
		JMenuItem modify_menu = new JMenuItem("重命名",modifyimage);
		modify_menu.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				rename_node();
			}
		});
		tree.add(modify_menu);
		
		TTMenuBar.add(file);
		TTMenuBar.add(tree);
	//	TTFrame.add(TTMenuBar);
		//TTFrame.add(TTMenuBar,BorderLayout.NORTH);
	}
	protected void rename_node() {
		// TODO Auto-generated method stub
		if(TTTree.getSelectionPath()!=null|LibTree.getSelectionPath()!=null)
		{
			//TTTree.startEditingAtPath(TTTree.getSelectionPath());
			editingJFrame.setVisible(true);
		}
		ui.reflashJtreeRoot();
	}
	protected void delete_node_from_tree() {
		// 删除选中节点
		JTree temptree = TTTree;
		DefaultMutableTreeNode temp_node = (DefaultMutableTreeNode)TTTree.getLastSelectedPathComponent();
		if(temp_node == null)
		{
			temp_node = (DefaultMutableTreeNode)LibTree.getLastSelectedPathComponent();
			temptree = LibTree;
		}	
		if(temp_node == null)
			return;
		DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode)temp_node.getParent();
		TopicEntry tempEntry = (TopicEntry)temp_node.getUserObject();
		lu.deleteWithAllChildrens(tempEntry);
		temp_node.removeFromParent();
		if(temptree == LibTree){
			libmodel.reload(lib_root);
			LibTree.setModel(libmodel);
			if(parent_node != lib_root)
			reload_TTTree(parent_node);
			else
			{
				root.removeAllChildren();
				model.reload();
				TTTree.setModel(model);
				TTTree.updateUI();
			}
		}
		else{
			model.reload(root);
			TTTree.setModel(model);
			try {
				reload_LibTrees();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			search_LibTree(parent_node,lib_root);
		}
		
		if(parent_node!=null)
		{
			DefaultTreeModel tempmodel = (DefaultTreeModel)temptree.getModel();
			temptree.setSelectionPath(new TreePath(tempmodel.getPathToRoot(parent_node)));
		}
		UpdateTree ut = new UpdateTree(System.currentTimeMillis());
		ut.oldName = ShorenUtils.getFullName(tempEntry);
		ut.change = 1;
		data.sendNotification(ut);
		
		ui.reflashJtreeRoot();
	}
	
	
	
	private void reload_TTTree(DefaultMutableTreeNode rootnode) {
		// 重新刷新右侧树
		TopicEntry rootentry = (TopicEntry)rootnode.getUserObject();
		
		root.setUserObject(rootentry);
		
		root.removeAllChildren();
		setTTTree(root);
		//TTTreeToWSNObject();

		model.reload(root);
		TTTree.setModel(model);
		TTTree.updateUI();
	}
	protected void add_node_to_tree() {
		// TODO Auto-generated method stub
		addJFrame.setVisible(true);
		//TTTree.setSelectionPath(null);
		//TTTree.updateUI();
	}
	private void ToolBar_init() {
		// 工具栏初始化
		TTToolBar = new JToolBar(SwingConstants.HORIZONTAL);
	    TTToolBar.setSize(1200, 30);
		
		JButton open_button = new JButton(newtreeimage);
		open_button.setToolTipText("新建主题树");
		open_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				newTreeFrame.setVisible(true);
			}
		});
		TTToolBar.add(open_button);
		
		JButton save_button = new JButton(saveimage);
		save_button.setToolTipText("保存");
		save_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				topic_counter = 0;
				Enumeration<DefaultMutableTreeNode> children = root.children();
				while(children.hasMoreElements()){
					DefaultMutableTreeNode child = children.nextElement();
					save_tree(child,"");
				}
			}
		});
		TTToolBar.add(save_button);
		
		JButton add_button = new JButton(addimage);
		add_button.setToolTipText("添加主题");
		add_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				add_node_to_tree();
			}
		});
		TTToolBar.add(add_button);
		
		JButton delete_button = new JButton(deleteimage);
		delete_button.setToolTipText("删除主题");
		delete_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				delete_node_from_tree();
			}
		});
		TTToolBar.add(delete_button);
		
		JButton modify_button = new JButton(modifyimage);
		modify_button.setToolTipText("重命名");	
		modify_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				rename_node();
			}
		});
		TTToolBar.add(modify_button);
		
		JButton close_button = new JButton(reflashimage);
		close_button.setToolTipText("刷新");	
		close_button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
//				TTFrame.setVisible(false);
				//从ldap库中重新下载主题树
				try {
					PSManagerUI.topicTreeManager.lu = new Ldap();
					PSManagerUI.topicTreeManager.lu.connectLdap(AdminMgr.ldapAddr,"cn=Manager,dc=wsn,dc=com","123456");
					PSManagerUI.topicTreeManager.reload_LibTrees();
					PSManagerUI.topicTreeM.invalidate();
				} catch (NamingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		TTToolBar.add(close_button);
		TTFrame.add(BorderLayout.NORTH, TTToolBar);
	}
	protected void new_tree(String newtree_name) throws NamingException {
		// 新建领域
		
		TopicEntry libentry = (TopicEntry)lib_root.getUserObject();
		TopicEntry treeentry = new TopicEntry();
		treeentry.setTopicName(newtree_name);
		treeentry.setTopicPath("ou="+newtree_name+","+libentry.getTopicPath());
		treeentry.setTopicCode(""+getTopicCode(newtree_name));
		DefaultMutableTreeNode newtree = new DefaultMutableTreeNode(treeentry);
		DefaultMutableTreeNode newroot = new DefaultMutableTreeNode(treeentry);
		
		lu.create(treeentry);
		
		lib_root.add(newroot);
		libmodel.reload(lib_root);
		LibTree.updateUI();
		reload_TTTree(newtree);
		TTTreeToWSNObject();
		data.sendNotification(new UpdateTree(System.currentTimeMillis()));
		
		newtree.setParent(lib_root);
		 TreePath visiblePath = new TreePath(libmodel.getPathToRoot(newtree));
		 LibTree.setSelectionPath(visiblePath);
		 LibTree.scrollPathToVisible(visiblePath);
		 LibTree.makeVisible(visiblePath);
		ui.reflashJtreeRoot();
	}
	protected void save_tree(DefaultMutableTreeNode current_node,String current_code) {
		// TODO Auto-generated method stub
	}
	public JPanel getTreeInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException {
		// 获取管理器窗口实例
		if( TTFrame == null )
		{    
			TTFrame = new JPanel();
			frame_init();
		}
		return TTFrame;
	}
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, NamingException {
		// TODO Auto-generated method stub
		TopicTreeManager tt = new TopicTreeManager(null);
//		JFrame frame = tt.getTreeInstance();
//		frame.setVisible(true);
//		frame.show();
		
		
		TopicEntry all = new TopicEntry("all", "1", 
				"ou=all_test,dc=wsn,dc=com", null);
		DefaultMutableTreeNode newtree = new DefaultMutableTreeNode(all);
		tt.lu = new Ldap();
		System.out.println("建立完成2");
		tt.lu.connectLdap("10.109.253.6","cn=Manager,dc=wsn,dc=com","123456");
		System.out.println("建立完成1");
		tt.setTTTree(newtree);
		System.out.println("建立完成");
		System.out.println(newtree);
//		try {
//			Socket s=new Socket();
//			s.connect(new InetSocketAddress("10.108.166.237",4001),5000);
//			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());	
//			oos.writeObject(new String("lookup the database!"));//发送
//			s.close();
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}