package com.weixin.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.weixin.db.MySQLBasic;
import com.weixin.util.CheckUtil;
import com.weixin.util.ConnUtil;
import com.weixin.util.MessageUtil;

public class WechatServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WechatServlet.class);
	static Hashtable<String, String> tb =null;
	private static   StringBuffer sb = new StringBuffer();
	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {
		if(tb == null){
			tb = new Hashtable<String, String>(1100);
			tb.put("0000", "999999----99");
		}
		logger.info(tb);
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.info(timestamp+","+nonce+","+signature+","+echostr);
		PrintWriter out = res.getWriter();
		if(CheckUtil.checksignature(signature, timestamp, nonce)){
			out.println(echostr);
			logger.info("verify pass!");
			logger.info(System.getProperty("user.dir"));
		}else{
			logger.info("verify failed!");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, 
			HttpServletResponse res) throws ServletException, IOException {
		//logger.info(req);
		String message=null;
		String tulinMsg = null;
		String regexAdd = "^ADD:[0-9]{4}----[0-9]{1,2}(----C)?$";
		String regexQuery = "^ROOT:LIST:[0-9]{4}$";
		String regexAction = "^AC-\\w{6}:[0-9]{4}$";
		res.setCharacterEncoding("UTF-8");//一定要在获取输出流之前
		PrintWriter out= res.getWriter();
		try {
			Map<String, String> map = MessageUtil.xmlToMap(req);
			String fromUserName=map.get("FromUserName");
			String toUserName=map.get("ToUserName");
			String msgType=map.get("MsgType");
			String content=map.get("Content");
			logger.info(content);
			if(MessageUtil.MESSADE_TEXT.equals(msgType)){//判断是文本类型消息
				//将用户消息给图灵API处理之后得到的回复，接下来交给工具封装为xml字符串，发送
				if(content.matches(regexAdd)){
					int c =content.indexOf(":");
					content = content.substring(c+1);
					String[] arr =content.split("----");
					String str=null,sql = null;
					if(arr.length>2 && arr[2].equals("C")){ 		//匹配强制改变标识
						str = MessageUtil.randomCharMd5().substring(0,6);
						
						logger.info(sql);
						//     UPDATE `wxdb` SET `times`=1, `access_code`='qqqqqq' WHERE (`code`=1234)
						/*------------------------synchronized---------------------------------*/
						final String str1=arr[1];
						final String str2=str;
						final String str3=arr[0];
						new Thread(){
							public void run(){
								dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
							}
							private void dosome() {
								String sql="UPDATE `wxdb` SET times="+str1+", access_code='"+str2+"' WHERE (code="+str3+")";
								MySQLBasic.executeAction(sql);
								sql="SELECT * FROM `wxdb` LIMIT 0, 1000";
								tb=MySQLBasic.executeAction(sql);
							}
						}.start();
					}else{
						sql="UPDATE `wxdb` SET `times`='"+arr[1]+"' WHERE (`code`='"+arr[0]+"')";
						MySQLBasic.executeAction(sql);
					}
					tb.put(arr[0],arr[1]+"----"+tb.get(arr[0]).split("----")[1]+"----"+tb.get(arr[0]).split("----")[2]+"----"+tb.get(arr[0]).split("----")[3]); //tb   3830:2----e6125d----Y    ADD:3311----4
					tulinMsg="AC-"+tb.get(arr[0]).split("----")[1]+":"+arr[0];
					logger.info("AC-"+tb.get(arr[0]).split("----")[1]+":"+arr[0]);
					
				}else if(content.matches(regexAction)){ // AC-adbn12:0000
					int c =content.indexOf("-");
					content = content.substring(c+1);  //  adbn12:0000
					String[] arr =content.split("\\:");
					if(tb==null){
						message=MessageUtil.intText(toUserName, fromUserName, "正在准备数据,请稍等...");
						
						Thread t2 = new Thread(){
							public void run(){
								dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
							}
							private void dosome() {
								String sql="SELECT * FROM `wxdb` LIMIT 0, 1000";
								tb=MySQLBasic.executeAction(sql);
							}
						};
						t2.start();
						
						if(tb.size()==1)message=MessageUtil.intText(toUserName, fromUserName, "请联系管理员微信:qq78732230");
						out.println(message);
						return;
					}
					if(tb.size()==1){
						message=MessageUtil.intText(toUserName, fromUserName, "正在准备数据,请稍等...");
						Thread t3= new Thread(){
							public void run(){
								dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
							}
							private void dosome() {
								String sql="SELECT * FROM `wxdb` LIMIT 0, 1000";
								tb=MySQLBasic.executeAction(sql);
							}
						};
						t3.start();
						if(tb.size()==1)message=MessageUtil.intText(toUserName, fromUserName, "请联系管理员微信:qq78732230");
						out.println(message);
						return;
					}
					if(tb.get(arr[1])==null){
						message=MessageUtil.intText(toUserName, fromUserName, "请用使用自己的推广码");
						out.println(message);
						return;
					}
					if(! tb.get(arr[1]).split("----")[1].equals(arr[0])){
						message=MessageUtil.intText(toUserName, fromUserName, "请不要修改标识");
						out.println(message);
						return;
					}
					/*
					logger.info(tb.get(arr[1]));
					logger.info(tb.get(arr[1]).split("----")[1]);
					logger.info(arr[0]);
					*/	
					int num;
					if((num=Integer.parseInt(tb.get(arr[1]).split("----")[0])) > 0){// AC-adbn12:0000   次数
						boolean result = ConnUtil.httpsAccess(arr[1]);
						num=result?num-1:num;
						tb.put(arr[1], num+"----"+ tb.get(arr[1]).split("----")[1]+"----"+tb.get(arr[1]).split("----")[2]+"----"+tb.get(arr[1]).split("----")[3]); // {0000,5----adbn12----Y----wx_id}
						
						final String str4=num+"";
						final String str5=arr[1];
						new Thread(){
							public void run(){
								dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
							}
							private void dosome() {
								String sql="UPDATE `wxdb` SET `times`='"+str4+"' WHERE (`code`='"+str5+"')";
								MySQLBasic.executeAction(sql);
							}
						}.start();
						
						
						tulinMsg = "→Result:" + result+" | Number: "+num+" Time ←";
					}else{
						if(tb.get(arr[1]).split("----").length>2 && tb.get(arr[1]).split("----")[2].equals("Y")){
							
							final String str6=arr[1];
							Thread t6 = new Thread(){
								public void run(){
									dosome();//运行dosome方法的线程是：Thread[Thread-0,5,main]
								}
								private void dosome() {
									String sql="UPDATE `wxdb` SET `experience`='N' WHERE (`code`='"+str6+"')";
									MySQLBasic.executeAction(sql);
								}
							};
							t6.start();
							
							tb.put(arr[1], num+"----"+ tb.get(arr[1]).split("----")[1]+"----"+tb.get(arr[1]).split("----")[2]+"----"+tb.get(arr[1]).split("----")[3]); // {0000,5----adbn12----N}
							tulinMsg = "推广码:"+arr[1]+":"+arr[0]+"-体验资格使用完毕,请充值";
						}else{
							tulinMsg = "推广码:"+arr[1]+":"+arr[0]+"-次数不足";
						}
					}
					
				}else if(content.matches(regexQuery)){
					String[] arr =content.split("\\:");
					tulinMsg = tb.get(arr[2]).toString();
				}else if(content.indexOf("体验") !=-1){
					tulinMsg = "请输入 4 位数字推广码".toString();
				}else if(content.matches("\\d{4}")){
//					String sql="SELECT * FROM `wxdb` WHERE code="+content;
//					Hashtable<String, String> life=MySQLBasic.executeAction(sql);
					if(tb.get(content) ==null){
						tulinMsg = "请正确输入自己的推广码".toString();
					}else{
						//   3830:2----e6125d----Y----wx_id
						if(tb.get(content).split("----")[2].equals("N")){
							tulinMsg = "你已经没有体验资格";
						}else{
							if(! tb.get(content).split("----")[3].equals(fromUserName)){
								tulinMsg = "恭喜您获得 "+tb.get(content).split("----")[0]+" 次免费下载机会,请复制AC开头的代码进行回复..."+"\n"+"AC-"+tb.get(content).split("----")[1]+":"+content.toString();
								//  fromUserName
								final String str1=fromUserName;
								final String str2=content;
								Thread t5 = new Thread(){
									public void run(){
										dosome(str1, str2);//运行dosome方法的线程是：Thread[Thread-0,5,main]
									}
									private void dosome(String fromUserName,String content) {
										String sql="UPDATE `wxdb` SET `wx_id`='"+fromUserName+"' WHERE (`code`='"+content+"')";
										MySQLBasic.executeAction(sql);
										sql="SELECT * FROM `wxdb` LIMIT 0, 1000";
										tb=MySQLBasic.executeAction(sql);
									}
								};
								t5.start();
								
							}else{
								tulinMsg = "此微信号已经体验完毕";
							}
						}
					}
				}
				else{
					tulinMsg = MessageUtil.tulinMsg(content);
				}
				message=MessageUtil.intText(toUserName, fromUserName, tulinMsg);
			}else if(MessageUtil.MESSADE_EVENT.equals(msgType)){
				if(tb == null){
					tb = new Hashtable<String, String>(1100);
					tb.put("0000", "999999----99----Y");
				}
				//第一次关注微信公众号会产生 事件,完成初始化 推送消息
				String eventType = map.get("Event");
				if(MessageUtil.MESSADE_SUBSCRIBE.equals(eventType)){
					message = MessageUtil.intText(toUserName, fromUserName, MessageUtil.menuText());
					//logger.info(message);
				}
			}

			/*RandomAccessFile raf = new RandomAccessFile("/home/bae/raf.dat","rw");
			for (File f : new File("/home/bae/").listFiles()) {
				System.out.println(f.getAbsolutePath());
			}*/
			logger.info("---------------------------------");
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally {
			out.close();
		}
	}
	
		
	
}
