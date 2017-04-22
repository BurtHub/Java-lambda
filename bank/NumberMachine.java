package com.huida.bank;

public class NumberMachine {
	//普通客户号码管理器
	private NumberManager commonManager=new NumberManager();
	//快速客户号码管理器
	private NumberManager fastManager=new NumberManager();
	//VIP客户号码管理器
	private NumberManager vipManager=new NumberManager();
	private NumberMachine(){}
	private static NumberMachine numberMachine=new NumberMachine();
	public static NumberMachine getnumberMachine(){
		return numberMachine;
	}
	public NumberManager getcommonManager(){
		return commonManager;
	}
	public NumberManager getfastManager(){
		return fastManager;
	}
	public NumberManager getvipManager(){
		return vipManager;
	}
}
