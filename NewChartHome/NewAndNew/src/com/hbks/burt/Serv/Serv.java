package com.hbks.burt.Serv;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.hbks.burt.ResourceInterface.MessageResourceHead;

public class Serv implements MessageResourceHead {
	private static JFrame fr;
	private static JButton bt, bt1;
	private static JLabel lb;
	private static JTextArea ta;
	private static JTextField tf, tf1;

	private ServerSocket Server;
	private ClientList cst = null;
	private ClientList cls = ClientList.cl;

	/*********************** 收发一体线程 ***************************************/
	/*
	 * 简单需求分析 将收到的信息进行分类后转发 具体分类 私聊 群聊
	 */

	/**
	 * 具体实现 遍历ClientList 查找具体联系人， 如果等于0直接进行群发
	 **/
	/* 头*************************接收消息线程**************************** */
	private void Receive(final String port, final Socket socket) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			public void run() {
				while (true) {
					BufferedReader bfr;
					try {
						bfr = new BufferedReader(new InputStreamReader(socket
								.getInputStream()));
						String s;
						while ((s = bfr.readLine()) != null) {
							if (s.length() > 0)
								ta.append(getTime() + " " + port + " " + s
										+ "\r\n");
							ReceiveCode(s);
						}
					} catch (IOException e) {}
					
				}

			}
		});
	}

	/************************** 消息处理模块**************************** */
	private void ReceiveCode(String s) {// 对收到的信息进行解码,并转发
		//会收到一条诡异的空消息，所以加限制
		if (s.length()!=0&&s != null) {
			int code = Integer.parseInt(s.substring(0, 1));
			int port=ManagerMessage;
			if(!s.contains("群聊")){
				port =Integer.parseInt(s.substring(1,6));
			}
			
			String Message = s.substring(1);
			switch (code) {
			case GroupMessage:// 群发
				ReceivedAndSend(0, GroupMessage + Message);
				break;
			case PrivateMessage:// 私聊
				ReceivedAndSend(port, PrivateMessage + Message);
				break;
			}
		}
	}

	public void ReceivedAndSend(int code, String msg) {// 信息转码，按照端口查找联系人并发送
		Set<Entry<String, Socket>> set = cls.GetMap();
		Iterator<Entry<String, Socket>> it = set.iterator();
		while (it.hasNext()) {
			String sq = it.next() + "";
			String a[] = sq.split("=");
			int port = Integer.parseInt(a[0]);
			if (code == GroupMessage) {
				//System.out.println(port+"群发");
				SendToClient(cls.SelClient(port + ""), msg);
			} else if (code == port) {
				SendToClient(cls.SelClient(port + ""), msg);
			}
		}
	}

	private void SendToClient(Socket client, String s) {// 发送消息到指定Socket
		//System.out.println(s);
		OutputStream out;
		try {
			out = client.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out), true);
			pw.println(s + "\r\n");
		} catch (IOException e) {
			System.out.println("转发异常");
		}

	}
	
	private String TraverseFriendList(){//指定联系人遍历好友列表并群发
		Set<Entry<String, Socket>> set = cls.GetMap();
		Iterator<Entry<String, Socket>> it = set.iterator();
		StringBuilder Message = new StringBuilder();
		while (it.hasNext()) {
			String sq = it.next() + "";
			String a[] = sq.split("=");
			int port = Integer.parseInt(a[0]);
			Message.append(port+":");
		}	
		return	new String(Message);
	}		
		
	
	
	private void SendToClientFriendList(){//进行遍历获取每个客户端（可否设置成递归）
		Set<Entry<String, Socket>> set = cls.GetMap();
		Iterator<Entry<String, Socket>> it = set.iterator();
		while (it.hasNext()) {
			String sq = it.next() + "";
			//System.out.println(sq+"好友列表");
			String a[] = sq.split("=");
			int port = Integer.parseInt(a[0]);
			String Flist=TraverseFriendList();
			if(Flist==null){
				SendToClient(cls.SelClient(port + ""), FriendList+" 没有好友在线");
			}else{
				SendToClient(cls.SelClient(port + ""), FriendList+Flist);
			}
			
		}		
			
		
	}
	/*********************************** 初始化模块 ******************************************/
	public void Create(int s) {
		try {
			Server = new ServerSocket(s);
		} catch (IOException e1) {
			ta.append(getTime() + " 服务器开启失败" + "\r\n");
		}
		cst = ClientList.cl;
		ExecutorService poolConnection = Executors.newSingleThreadExecutor();
		poolConnection.execute(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket socket = Server.accept();
						String port = socket.getPort() + "";
						cst.AddClient(port, socket);// 添加到客户列表中
						SendToClient(socket, ManagerMessage+"欢迎加入聊天室");
						Receive(port, socket);
						ta.append(getTime() + " " + port + " 已连接" + "\r\n");
						// 连接后给每个客户端发送好友列表
						SendToClientFriendList();
					}
				} catch (Exception e) {
					e.printStackTrace();
					ta.append(getTime() + " 客户端连接异常" + "\r\n");
				}
			}

		});
		ta.append(getTime() + " 服务器开启成功" + "\r\n");
	}

	private void addListener() {

		bt.addMouseListener(new MouseAdapter() {// 联网
			public void mouseClicked(MouseEvent e) {
				Create(Integer.parseInt(tf.getText()));
			}
		});

		bt1.addMouseListener(new MouseAdapter() {// 单击发送
			public void mouseClicked(MouseEvent e) {

			}
		});

		bt1.addKeyListener(new KeyAdapter() {// 按回车键发送
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

				}
			}
		});
		fr.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return (df.format(new Date()));// new Date()为获取当前系统时间
	}

	public void init() {
		fr = new JFrame("服务器");
		fr.setLayout(null);
		lb = new JLabel(" 请输入端口:");

		tf = new JTextField();
		tf.setText("5012");
		tf1 = new JTextField();
		ta = new JTextArea();
		bt = new JButton("开启服务器");
		bt1 = new JButton("发送");
		fr.setBounds(700, 110, 427, 381);

		fr.getContentPane().add(lb);
		lb.setBounds(10, 10, 90, 24);

		fr.getContentPane().add(tf);
		tf.setBounds(120, 10, 50, 24);

		fr.getContentPane().add(bt);
		bt.setBounds(281, 10, 112, 24);

		fr.getContentPane().add(ta);
		ta.setBounds(10, 50, 390, 230);

		fr.getContentPane().add(tf1);
		tf1.setBounds(10, 290, 240, 30);

		fr.getContentPane().add(bt1);
		bt1.setBounds(280, 290, 130, 30);
		addListener();

		fr.setVisible(true);
	}
}
