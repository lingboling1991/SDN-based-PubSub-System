package com.bupt.wangfu.Swing;



	/**
	 * @param args
	 */
	import java.awt.*;
	import java.awt.event.*;
	import javax.swing.*;
	    /*����ChangeEvent������Swing���¼���������AWT���¼������import Swing���¼���������
	     *ChangeEvent�¼���
	     */
	import javax.swing.event.*;

	public class JTabbedPane2 implements ActionListener,ChangeListener{
	  int index=0;
	  int newNumber=1;
	  JTabbedPane tabbedPane=null;
	  public JTabbedPane2(){
	     JFrame f=new JFrame("JTabbedPane2");	
	     Container contentPane=f.getContentPane();
	     
	     JLabel label1=new JLabel(new ImageIcon(".\\icons\\flower.jpg"));
	     JPanel panel1=new JPanel();
	     panel1.add(label1);
	     
	     JLabel label2=new JLabel("Label 2",JLabel.CENTER);
	     label2.setBackground(Color.pink);
	     label2.setOpaque(true);
	     JPanel panel2=new JPanel();
	     panel2.add(label2);
	     
	     JLabel label3=new JLabel("Label 3",JLabel.CENTER);
	     label3.setBackground(Color.yellow);
	     label3.setOpaque(true);
	     JPanel panel3=new JPanel();
	     panel3.add(label3);
	     
	     tabbedPane=new JTabbedPane();
	     tabbedPane.setTabPlacement(JTabbedPane.TOP);//���ñ�ǩ�÷�λ�á�
	    /*����ChangeEvent������Swing���¼���������AWT���¼������import Swing���¼���������
	     *ChangeEvent�¼���
	     */
	     tabbedPane.addChangeListener(this);
	     tabbedPane.addTab("Picture",null,panel1,"ͼ��");
	     tabbedPane.addTab("Label 2",panel2);
	     tabbedPane.addTab("Label 3",null,panel3,"label");
	     tabbedPane.setEnabledAt(2,false);//��Label 3��ǩΪDisable״̬
	     JButton b=new JButton("������ǩ");
	     b.addActionListener(this);
	     contentPane.add(b,BorderLayout.SOUTH);
	     contentPane.add(tabbedPane,BorderLayout.CENTER);
	     
	     f.pack();
	     f.show();
	     f.addWindowListener(new WindowAdapter(){
	               public void WindowClosing(WindowEvent e){
	                  System.exit(0);
	               }
	      });     
	  }	
	/*ʵ��ChangeListener������Ŀ����ʹ����ߵı�ǩ�е�ѡ�����ұߵı�ǩ�Ż���ʾEnable״̬��getSelectedIndex()�����ɷ���
	 *Ŀǰ��ѡ��ǩ��indexֵ��getTabCount()�����ɷ���JTabbedPane��Ŀǰ���м�����ǩ����setEnabledAt()��������ʹĳ����ǩ
	 *��״̬ΪEnable��Disable(trueΪEnable,falseΪDisable).
	 */
	  public void stateChanged(ChangeEvent e){
	     if (index!=tabbedPane.getSelectedIndex()){
	         if(index<tabbedPane.getTabCount()-1)
	            tabbedPane.setEnabledAt(index+1,true);
	     }
	     index=tabbedPane.getSelectedIndex();
	  }
	/*ʵ��ActionListener�ӿ�,���û�����"������ǩ"��ťʱ����
	 *����tabbedPane������һ��Disable״̬�ı�ǩ��
	 */
	  public void actionPerformed(ActionEvent e){
	      JPanel pane1=new JPanel();
	      JLabel label4=new JLabel("new label"+newNumber,JLabel.CENTER);
	      label4.setOpaque(true);
	      pane1.add(label4);
	      tabbedPane.addTab("new "+newNumber,pane1);
	      tabbedPane.setEnabledAt(newNumber+2,false);
	      newNumber++;
	      tabbedPane.validate();
	  }
	  public static void main(String[] args){
	    new JTabbedPane2();
	  }
	

}
