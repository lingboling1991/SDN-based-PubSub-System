package com.bupt.wangfu.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapUtil {
	//���ڴ洢�������ݿ����Ϣ 
	private Hashtable<String, String> env = null;
	//Ldap �����Ĳ�������
	private LdapContext ctx = null;
	public LdapUtil(){
		env = new Hashtable<String, String>();
	}
	//����һ��OpenLdap���ݿ������
	public void connectLdap() throws NamingException{
		//set the initializing information of the context
		env.put(Context.INITIAL_CONTEXT_FACTORY,  "com.sun.jndi.ldap.LdapCtxFactory");
		//set the URL of ldap server
		env.put(Context.PROVIDER_URL, "ldap://localhost:389");
		//set the authentication mode
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		//set user of ldap server
		env.put(Context.SECURITY_PRINCIPAL, "cn=Manager,dc=maxcrc,dc=com");
		//set password of user
		env.put(Context.SECURITY_CREDENTIALS, "123456");
		
		//initialize the ldap context
		ctx = new InitialLdapContext(env, null);
	}
	//�ر�һ��OpenLdap����
	public void closeContext() throws NamingException{
		ctx.close();
	}
	//��ȡһ��Ldap�����Ĳ�������
	public LdapContext getContext(){
		return  this.ctx;
	}
	
	public void isExist(TopicEntry te) throws NamingException{
		ctx.lookup(te.getTopicPath());
	}
	/**
	 * �½�һ��ou��Ŀ
	 * @param newOUName	�½���Ŀ������
	 * @param dest_path	�½���Ŀ�ĸ��ڵ�
	 * @param ctx	LdapContext��������
	 */
	public void createOUEntry(TopicEntry new_topicEntry){
		Attributes attrs = new BasicAttributes();
		attrs.put("ou", new_topicEntry.getTopicName());
		BasicAttribute objectclassSet = new BasicAttribute("objectclass");
		objectclassSet.add("top");
		objectclassSet.add("organizationalUnit");
		attrs.put(objectclassSet);
		try {
			ctx.createSubcontext(new_topicEntry.getTopicPath(),attrs);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * �½�һ�����������Ե�ou��Ŀ
	 * @param newOUName	�½���Ŀ������
	 * @param dest_path	�½���Ŀ�ĸ��ڵ�
	 * @param topicCode	����Ŀ�ı���
	 * @param ctx	LdapContext��������
	 */
	public void createOUEntryWithTopicCode(TopicEntry new_topicEntry){
		Attributes attrs = new BasicAttributes();
		attrs.put("ou", new_topicEntry.getTopicName());
		BasicAttribute objectclassSet = new BasicAttribute("objectclass");
		objectclassSet.add("top");
		objectclassSet.add("organizationalUnit");
		attrs.put(objectclassSet);
		attrs.put("description", new_topicEntry.getTopicCode());
		try {
			ctx.createSubcontext(new_topicEntry.getTopicPath(),attrs);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ɾ��һ��ָ����Ŀ������Ŀ������Ҷ�ӽڵ㣩
	 * @param goal_path	��ɾ����Ŀ��λ��
	 * @param ctx	Ldap �����Ĳ�������
	 */
	public void delete(TopicEntry te){
		try {
			ctx.destroySubcontext(te.getTopicPath());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * ɾ��һ��ָ����Ŀ������������Ŀ������Ŀ������Ҷ�ӽڵ㣩
	 * @param goal_path	��ɾ����Ŀ��λ��
	 * @param lc	Ldap �����Ĳ�������
	 * @throws NamingException
	 */
	public void deleteWithAllChildrens(TopicEntry te) throws NamingException{
		List<TopicEntry> delete = getWithAllChildrens(te);
		for(int i=delete.size()-1;i>=0;i--){
			ctx.destroySubcontext(delete.get(i).getTopicPath());
		}
	}
	/**
	 * ��ȡ�����ַ���
	 * @param nameWithOU	��ou�������ַ��������磺"ou=topic"
	 * @return	����ou�������ַ��������磺"topic"
	 */
	public String getTopicString(String nameWithOU){
		return nameWithOU.substring(3, nameWithOU.length());
	}
	/**
	 * ��װ����
	 * @param path	OpenLDAP����Ŀ��·�����磺"ou=subTopic,ou=topic"
	 * @return	������OpenLDAP·�����磺"ou=subTopic,ou=topic,dc=wsn,dc=com"
	 */
	public String wrapper(String path, String _wrapper){
		return path + "," + _wrapper;
	}
	/**
	 * ���ظ�����Ŀ������������Ŀ
	 * @param topic_path	������Ŀ��·��
	 * @param lc	Ldap �����Ĳ�������
	 * @return	��������Ŀ������������Ŀ�洢���б��з���
	 * @throws NamingException 
	 */
	public List<TopicEntry> getWithAllChildrens(TopicEntry te) throws NamingException{
		List<TopicEntry> childrens = new ArrayList<TopicEntry>();
		//����ǰ��Ŀ���뵽�б���
		TopicEntry root = new TopicEntry();
		root.setTopicName(getTopicString(te.getTopicPath().split(",")[0]));
		Attributes attrs = ctx.getAttributes(te.getTopicPath());
		root.setTopicCode(attrs.get("description").toString().split(": ")[1]);
		root.setTopicPath(te.getTopicPath());
		childrens.add(root);
		//����ǰ��Ŀ����������Ŀ������ӵ��б���
		Queue<TopicEntry> queue = new LinkedList<TopicEntry>();
		queue.offer(root);
		while(!queue.isEmpty()){
			TopicEntry temp = queue.poll();
			List<TopicEntry> list = getSubLevel(temp);
			if(!list.isEmpty()){
				for(int i=0;i<list.size();i++){
					childrens.add(list.get(i));
					queue.offer(list.get(i));
				}
			}
		}
		return childrens;
	}
	/**
	 * ���ظ�����Ŀ��һ������������Ŀ
	 * @param topic_path	������Ŀ��·��
	 * @param lc	Ldap �����Ĳ�������
	 * @return	��������Ŀ��һ������������Ŀ�洢���б��з���
	 */
	public List<TopicEntry> getSubLevel(TopicEntry te) throws NamingException{
		List<TopicEntry> sub = new ArrayList<TopicEntry>();
		String sub_path = null;
		
		NamingEnumeration<NameClassPair> x = ctx.list(te.getTopicPath());
		while(x.hasMore()){
			TopicEntry _te = new TopicEntry();
			_te.setTopicName(getTopicString(x.next().getName()));
			
			sub_path = "ou=" + _te.getTopicName() + "," + te.getTopicPath();
			Attributes attrs = ctx.getAttributes(sub_path);
			
			_te.setTopicCode(attrs.get("description").toString().split(": ")[1]);
			_te.setTopicPath(sub_path);
			
			sub.add(_te);
		}
		return sub;
	}

	/**
	 * ������������Ϊ���������
	 * @param goal_path	Ҫ��ӱ������Ե�����
	 * @param topicCode	����
	 * @param lc	LdapContext��������
	 * @throws NamingException	
	 */
	public void addTopicCode(TopicEntry te, String topicCode) throws NamingException{
		 ModificationItem[] mods =new ModificationItem[1];
		 Attribute attr = new BasicAttribute("description");
		 attr.add(topicCode);
		 mods[0] = new ModificationItem(LdapContext.ADD_ATTRIBUTE, attr);
		 
		 ctx.modifyAttributes(te.getTopicPath(), mods);
	}
	/**
	 * �޸�����ı�������
	 * @param goal_path	Ҫ�޸ĵ�����
	 * @param new_topicCode	�µı���
	 * @param lc	LdapContext��������
	 * @throws NamingException
	 */
	public void modifyTopicCode(TopicEntry te, String new_topicCode) throws NamingException{
		 ModificationItem[] mods =new ModificationItem[1];
		 Attribute attr = new BasicAttribute("description");
		 attr.add(new_topicCode);
		 mods[0] = new ModificationItem(LdapContext.REPLACE_ATTRIBUTE, attr);
		 
		 ctx.modifyAttributes(te.getTopicPath(), mods);
	}
	/**
	 * ɾ������ı�������
	 * @param goal_path	��ɾ���������Ե�����
	 * @param lc	LdapContext��������
	 * @throws NamingException
	 */
	public void removeTopicCode(TopicEntry te) throws NamingException{
		 ModificationItem[] mods =new ModificationItem[1];
		 Attribute attr = new BasicAttribute("description");
		 mods[0] = new ModificationItem(LdapContext.REMOVE_ATTRIBUTE, attr); 
		 ctx.modifyAttributes(te.getTopicPath(), mods);
	}
	/**
	 * ������һ��Ҷ�ӽڵ�
	 * @param te	������Ŀ��ڵ㣨������Ҷ�ӽڵ㣬���򷵻ؿ�ָ�룩
	 * @param new_name	������
	 * @param lc	LdapContext �����Ĳ�������
	 * @return	����������֮��Ľ��
	 * @throws NamingException
	 */
	public TopicEntry rename(TopicEntry te, String new_name) throws NamingException{
		List<TopicEntry> ls = getSubLevel(te);
		if(ls.isEmpty()){
			String old_name = te.getTopicName();
			String old_path = te.getTopicPath();
			String new_path = old_path.replaceFirst(old_name, new_name);
			ctx.rename(old_path, new_path);
			te.setTopicName(new_name);
			te.setTopicPath(new_path);
			return te;
		}else{
			return null;
		}
	}
}


















