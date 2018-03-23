package com.weixin.servlet;

import com.weixin.db.MySQLBasic;

public class Test {

	public static void main(String[] args) {
		int i;
		for ( i = 0; i < 10000000; i++) {
			System.out.println("");
		}
		
		final int str = i;
		new Thread(){
			public void run(){
				try {
					Thread.sleep(1000);
					dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			private void dosome() {
				System.out.println(str);
				return;
			}
		}.start();
		
	}

}
