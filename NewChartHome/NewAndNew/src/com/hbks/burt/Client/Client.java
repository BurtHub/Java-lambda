package com.hbks.burt.Client;

import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hbks.burt.ResourceInterface.MessageResourceHead;

public class Client implements MessageResourceHead {
	private static JFrame fr;
	private static JButton bt, bt1;
	private static JLabel lb, lb1;
	private static List jl;
	private static JTextArea ta;
	private static JTextField tf, tf1;

	private Socket Client = null;
	private String LocalPort=null;

	/*************************** 收发处理方法 ****************************/

	private void ReceiveCode(String s) { // 判断消息类型更新UI消息
		//会收到一条诡异的空消息，所以加限制
		if (s.length()!=0&&s != null) {
			int code = Integer.parseInt(s.substring(0, 1));
			switch (code) {
			case GroupMessage:					
				ta.append(getTime() + " " + s.substring(1) + "\r\n");
				break;
			case PrivateMessage:
				ta.append(getTime() + " " + s.substring(1) + "\r\n");
				break;
			case FriendList:
				jl.removeAll();
				jl.add("群聊");
				
				//解析服务器发送的好友列表数据包
				String FList=s.substring(1);
				String[] ports=FList.split(":", 0);
				for(String i:ports){
					if((!i.equals(LocalPort))&&i.length()>0)
					jl.add(i);
				}
				jl.select(0);
				break;
			case ManagerMessage:
				ta.append(getTime()+" 管理员：" + s.substring(1)+ "\r\n");
				break;
			}
		}
	}

	private boolean Connection() {
		try {
			Client = new Socket("127.0.0.1", Integer.parseInt(tf.getText()));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**************************接收消息线程*****************************/
	private void Receive() {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			public void run() {
				while (true) {
					BufferedReader bfr;
					try {
						bfr = new BufferedReader(new InputStreamReader(Client
								.getInputStream()));
						String s=null;
						while ((s = bfr.readLine()) != null) {
							System.out.println(s);
							ReceiveCode(s);
						}
						
					} catch (IOException e) {
					}
				}
			}
		});
	}
	/************************** 本地好友列表检索(线程) *****************************/
	/*private void CheckFriendList(){//使用排序去重算法
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			public void run() {
				while (true) {
					try {Thread.sleep(500);} catch (InterruptedException e) {}
					String[] items = jl.getItems();
					int start=0;
					int end=items.length-1;
					while(items.length>0){
						
					}
				
				}
			}
		});
	}*/
	/************************** 发送消息(线程) *****************************/
	private void SendToServ(String s) throws IOException {
		OutputStream out = Client.getOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out), true);
		pw.println(s + "\r\n");
	}

	/************************** 信息转码器 *****************************/
	private String SendCode(String s) {
		if (s.contains("群聊")) {
			return GroupMessage + s;
		} else {
			return PrivateMessage + s;
		}
	}

	private String getMessage() {
		String code = SendCode(jl.getSelectedItem());
		String s = tf1.getText();
		tf1.setText(null);
		if (s.length() > 0) {
			//ta.append(getTime() + " 自己：" + s + "\r\n");
			return code + ":" + s;
		} else {
			tf1.setToolTipText("聊天测试内容");
			return null;
		}
	}

	private String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return (df.format(new Date()));// new Date()为获取当前系统时间
	}

	/************************** 界面监听器 *****************************/
	private void addListener() {
		fr.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		bt.addMouseListener(new MouseAdapter() {// 联网
			public void mouseClicked(MouseEvent e) {
				if (Connection()) {
					Receive();
					LocalPort=Client.getLocalPort()+"";
					fr.setTitle("编号"+LocalPort);
					lb.setText("连接成功");
					bt.setEnabled(false);
				} else {
					lb.setText("连接失败");
				}
			}
		});

		bt1.addMouseListener(new MouseAdapter() {// 单击发送
			public void mouseClicked(MouseEvent e) {
				try {
					String s = getMessage();
					if (s != null) {
						SendToServ(s);
					}

				} catch (IOException e1) {
					System.out.println("发送异常");
				}
			}
		});

		bt1.addKeyListener(new KeyAdapter() {// 按回车键发送
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						String s = getMessage();
						if (s.length() > 0)
							SendToServ(s);
					} catch (IOException e1) {
						System.out.println("发送异常");
					}

				}
			}
		});

	}

	/******************* 88888888888**********界面部分**********88888888888 ****************************/

	public void init() {
		fr = new JFrame("聊天窗口");
		fr.setLayout(null);
		lb = new JLabel("服务器未连接： 请输入端口");
		lb1 = new JLabel("在线好友");
		jl = new List();
		jl.add("群聊");
		jl.select(0);
		tf = new JTextField();
		tf.setText("5012");
		tf1 = new JTextField();
		ta = new JTextArea();
		bt = new JButton("联网");
		bt1 = new JButton("发送");
		fr.setBounds(100, 150, 481, 371);

		fr.getContentPane().add(lb);
		lb.setBounds(10, 10, 170, 24);

		fr.getContentPane().add(lb1);
		lb1.setBounds(370, 10, 120, 24);

		fr.getContentPane().add(tf);
		tf.setBounds(190, 10, 50, 24);

		fr.getContentPane().add(bt);
		bt.setBounds(251, 10, 112, 24);

		fr.getContentPane().add(ta);
		ta.setBounds(10, 50, 300, 230);

		fr.getContentPane().add(tf1);
		tf1.setBounds(10, 290, 300, 30);

		fr.getContentPane().add(jl);
		jl.setBounds(322, 50, 130, 230);

		fr.getContentPane().add(bt1);
		bt1.setBounds(322, 290, 130, 30);
		addListener();

		tf1.setText("聊天测试内容");
		fr.setVisible(true);
	}

}
