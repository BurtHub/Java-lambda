package com.huida.bank;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainTest {

	public static void main(String[] args) {
		for(int i=0;i<4;i++){
			ServiceWindow Window=new ServiceWindow();
			Window.setid(i+1);
			Window.start();
		}
		ServiceWindow fastWindow=new ServiceWindow();
		fastWindow.setType(Type.fast);
		fastWindow.setid(5);
		fastWindow.start();
		
		ServiceWindow vipWindow=new ServiceWindow();
		vipWindow.setType(Type.vip);
		vipWindow.setid(6);
		vipWindow.start();
		
		ScheduledExecutorService timer=Executors.newScheduledThreadPool(3);
		timer.scheduleAtFixedRate(new Runnable(){
			public void run(){
				int num=NumberMachine.getnumberMachine().getcommonManager().generaterNubmer();;
				System.out.println("第"+num+"普通客户等待服务......");
			}
		}, Time.common, Time.common, TimeUnit.SECONDS);
		timer.scheduleAtFixedRate(new Runnable(){
			public void run(){
				int num=NumberMachine.getnumberMachine().getfastManager().generaterNubmer();;
				System.out.println("第"+num+"快速客户等待服务......");
			}
		}, Time.common*2, Time .common*2, TimeUnit.SECONDS);
		timer.scheduleAtFixedRate(new Runnable(){
			public void run(){
				int num=NumberMachine.getnumberMachine().getvipManager().generaterNubmer();;
				System.out.println("第"+num+"vip客户等待服务......");
			}
		}, Time.common*6, Time.common*6, TimeUnit.SECONDS);
	}

}
