package MainActivity;

import java.awt.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



/*****************88888************客户端代码*****************8888888*********************/

public class CliIntent {
	private JFrame fr;
	private JButton bt,bt1;
	private JLabel lb,lb1;
	private List jl;
	private JTextArea ta;
	private JTextField tf,tf1;
	ClientList cls=ClientList.cl;
	
	CliIntent(){
		Init();
	}
/****************8888888*************客户端连接部分**************8888888************************/
	 class InnerClient{
		//获取客户列表，在单击连接按钮时，将所有列表中的客户添加到List中
		
		private Socket Client=null;
		InnerClient() { }
		
		boolean Connection(){
			try {
				Client=new Socket("127.0.0.1",Integer.parseInt(tf.getText()));
				ReceivePool();
				
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		//不死线程(接收部分,使用线程池)
		void ReceivePool(){
			ExecutorService pool=Executors.newSingleThreadExecutor();
			pool.execute(new Runnable() {
				public void run() {
					BufferedReader bfr;
					try {
						bfr=new BufferedReader(new InputStreamReader(Client.getInputStream()));
						String s;
						while((s=bfr.readLine())!=null){
							ta.append(s+"\r\n");
						}
					} catch (IOException e) {}
					
				}
			});
		}
		
		//群聊和私聊功能，将List中的端口号转为int类型后发送给服务器
		
	}
/************8888888*****************监听器部分****************88888888888**********************/
	void FlushFriendList(){  //刷新好友列表
		Set<Entry<String, Socket>> set=cls.GetMap();
		Iterator<Entry<String, Socket>> it=set.iterator();
		while(it.hasNext()){
			String s=it.next()+"";
			String a[]=s.split("=");
			String[] lists=jl.getItems();
			boolean flag=true;
			for(String a1:lists){
				if(a[0].equals(a1))flag=false;
			}
			if(flag)jl.add(a[0]);
		}
	}
	void SendToallFriend(String s) throws NumberFormatException, IOException{
		Set<Entry<String, Socket>> set=cls.GetMap();
		Iterator<Entry<String, Socket>> it=set.iterator();
		while(it.hasNext()){
			String sq=it.next()+"";
			String a[]=sq.split("=");
			SendToFriend(Integer.parseInt(a[0]),s);
			
		}
	}
	void SendToFriend(int code,String s) throws IOException{
		Socket sendSocket=cls.SelClient(code+"");
		OutputStream out=sendSocket.getOutputStream();
		PrintWriter pw=new PrintWriter(new OutputStreamWriter(out),true);
		pw.println(code+":"+s+"\r\n");
	}
	
	void addListener(){
		bt.addMouseListener(new MouseAdapter() {//联网
			public void mouseClicked(MouseEvent e) {
				if(bt.getText()=="刷新好友列表"||bt.getText()=="连接成功"){
					FlushFriendList();
					return;
					}
				if(new InnerClient().Connection()){
					lb.setText("连接成功");
					bt.setText("刷新好友列表");
					FlushFriendList();
				
				}else
					lb.setText("好友列表成功刷新");
					FlushFriendList();
				
			}
		});
		
		bt1.addMouseListener(new MouseAdapter() {//单击发送
			public void mouseClicked(MouseEvent e) {
				String s=tf1.getText();
				tf1.setText(null);
				
				try{
					if(jl.getSelectedItem().equals("群聊")){
						SendToallFriend(s);
						ta.append("群聊"+":"+s+"\r\n");
					}else{
						int code=Integer.parseInt(jl.getSelectedItem());
						SendToFriend(code,s);
						ta.append(code+":"+s+"\r\n");
					
					}
				}catch(Exception e1){}
				System.out.println("发送失败");
			}
		});
		
		bt1.addKeyListener(new KeyAdapter() {//按回车键发送
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					String s=tf1.getText();
					tf1.setText(null);
					
					try{
						if(jl.getSelectedItem().equals("群聊")){
							SendToallFriend(s);
							ta.append("群聊"+":"+s+"\r\n");
						}else{
							int code=Integer.parseInt(jl.getSelectedItem());
							SendToFriend(code,s);
							ta.append(code+":"+s+"\r\n");
						
						}
					}catch(Exception e1){}
					System.out.println("发送失败");
					
				}
			}
			
		});
		
	}	
/*******************88888888888**********界面部分**********88888888888****************************/
	void Init(){
		
		fr=new JFrame("聊天窗口");
		fr.setLayout(null);
		lb=new JLabel("服务器未连接： 请输入端口");
		lb1=new JLabel("在线好友");
		jl=new List();
		jl.add("群聊");
		jl.select(0);
		tf=new JTextField();
		tf1=new JTextField();
		ta=new JTextArea();
		bt=new JButton("联网");
		bt1=new JButton("发送");
		fr.setBounds(100, 150,481, 371);
		
		fr.getContentPane().add(lb);
		lb.setBounds(10,10, 170, 24);
		
		fr.getContentPane().add(lb1);
		lb1.setBounds(370,10, 120, 24);
		
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
		
		fr.setVisible(true);
	}
	

}
