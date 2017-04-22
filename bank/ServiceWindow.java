package com.huida.bank;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceWindow {
	private Type type=Type.common;
	private int id=1;//窗口
	Integer commonnum=null;//普通用户编号
	Integer fastnum=null;//快速用户编号
	Integer vipnum=null;//vip用户编号
	public void setType(Type type){
		this.type=type;
	}
	public void setid(int id){
		this.id=id;
	}
	public void start(){
		ExecutorService pool=Executors.newSingleThreadExecutor();
		pool.execute(new Runnable(){
			public void run(){
				while(true){
				switch(type){
				case common:
					common();
					break;
				case fast:
					fast();
					break;
				case vip:
					vip();
					break;
				} 
				}
			}
		});
	}
	//定义处理普通客户逻辑方法
	public void common(){
		commonnum=NumberMachine.getnumberMachine().getcommonManager().getNumber();
		if(commonnum!=null){
			System.out.println("第"+id+"个"+type+"窗口准备为"+commonnum+"客户服务");
		long startTime=System.currentTimeMillis();
		int time=Time.MAX_TIME-Time.MIN_TIME+1;
		int randTime=new Random().nextInt(time)+Time.MIN_TIME;//nextInt()0-9之间的随机数
		try {
			Thread.sleep(randTime*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime=System.currentTimeMillis();
		long serviceTime=endTime-startTime;
		System.out.println("第"+id+"个"+type+"窗口为"+commonnum+"客户服务"+(serviceTime/1000)+"秒");
		}else{
			System.out.println("没有普通客户,先休息一秒");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void fast(){
		fastnum=NumberMachine.getnumberMachine().getfastManager().getNumber();
		if(fastnum!=null){
			System.out.println("第"+id+"个"+type+"窗口准备为"+fastnum+"客户服务");
		long startTime=System.currentTimeMillis();
		try {
			Thread.sleep(Time.MIN_TIME*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime=System.currentTimeMillis();
		long serviceTime=endTime-startTime;
		System.out.println("第"+id+"个"+type+"窗口为"+fastnum+"客户服务"+(serviceTime/1000)+"秒");
		}else{
			System.out.println("没有快速客户，要准备为普通客户服务");
			common();
		}		
	}
	public void vip(){
		vipnum=NumberMachine.getnumberMachine().getvipManager().getNumber();
		if(vipnum!=null){
			System.out.println("第"+id+"个"+type+"窗口准备为"+vipnum+"客户服务");
		long startTime=System.currentTimeMillis();
		int time=Time.MAX_TIME-Time.MIN_TIME+1;
		int randTime=new Random().nextInt(time)+1+Time.MIN_TIME;
		try {
			Thread.sleep(randTime*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime=System.currentTimeMillis();
		long serviceTime=endTime-startTime;
		System.out.println("第"+id+"个"+type+"窗口为"+vipnum+"客户服务"+(serviceTime/1000)+"秒");
	}else{
		System.out.println("没有vip客户，要准备为普通客户服务");
		common();
		}
	}
}
