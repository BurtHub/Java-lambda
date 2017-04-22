package MainActivity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servicer {
	private ServerSocket Server;
	private ClientList cst=null;
	private Socket socket;
	public Servicer(int s) throws IOException{
		Server=new ServerSocket(s);
		cst=ClientList.cl;
		
		//new Thread(new Repeat(cst.SelClient(ip))).start();
		//添加客户端到ClientList中
	/********************************连接线程**************************************/	
		
		ExecutorService poolConnection=Executors.newSingleThreadExecutor();
		poolConnection.execute(new Runnable() {
			public void run() {
				try{
				while(true){
					socket=Server.accept();
					String  port=socket.getPort()+"";
					cst.AddClient(port,socket);//添加到客户列表中
				}	
				}catch(Exception e){}
			}
			
		});
		System.out.println("服务器正常开启");
			//具有端口选择功能，收到小于1024的时候，为群发
	/*************************************接收并转发线程******************************************/
	/*	
	ExecutorService PoolRepeat=Executors.newSingleThreadExecutor();
	PoolRepeat.execute(new Runnable() {
		
		public void run() {
			
			InputStreamReader isr = null;
			BufferedReader br = null;
			try{
				isr=new InputStreamReader(socket.getInputStream());
				br =new  BufferedReader(isr);
				while((br.readLine())!=null){
					//接收功能
					isr=new InputStreamReader(socket.getInputStream());
					br =new  BufferedReader(isr);
					System.out.println(br.readLine());
					System.out.println("服务器开始发送");
					OutputStream out=socket.getOutputStream();
					PrintWriter pw=new PrintWriter(new OutputStreamWriter(out),true);
					int q=socket.getPort();
					pw.write(q+"登入");
					System.out.println("发送完毕");
					pw.flush();
				
				}
			}catch(Exception e){}finally{
				try{
					br.close();
					isr.close();
				}catch(Exception e){}
				
			}
			
			
		}
	});*/
	
	
	
	}
	
	
/*	class Connection implements Runnable{
		public void run() {
			PrintWriter pw = null;
			try{
			while(true){
				Socket socket=Server.accept();
				pw=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
				String  port=socket.getPort()+"";
				pw.println("端口："+port);
				pw.flush();
						}	
			}catch(Exception e){
				
			}finally{
				pw.close();
				System.out.println("接收关闭");
			}
		}
	}*/

}
