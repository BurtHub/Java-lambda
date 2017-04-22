

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

//数据框架,要求唯一，所以使用单例模式
public class ClientList {    //目前存放端口号， 可以 存放IP地址
	public static ClientList cl =new ClientList();
	private ClientList(){}
	private TreeMap<String,String> Tree=new TreeMap<String,String>();
	
	public void AddClient(String ip,String client){
		Tree.put(ip, client);
	}
	public void DelClient(String ip){//移除对应的客户端
		Tree.remove(ip);
	}
	public String SelClient(String ip){//获取对应的Socket
		return Tree.get(ip);
	}
	
	public Set<Entry<String, String>> GetMap(){//返回所有客户端端口号
		return Tree.entrySet();
	}
}
