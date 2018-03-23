package com.weixin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.weixin.entity.TextMessage;

import net.sf.json.JSONObject;
/**
 * 用于处理消息的工具类
 * @author xiao
 *
 */
public class MessageUtil {
	public static final String MESSADE_TEXT="text";
	public static final String MESSADE_IMAGE="image";
	public static final String MESSADE_VOICE="voice";
	public static final String MESSADE_MUSIC="music";
	public static final String MESSADE_VIDEO="video";
	public static final String MESSADE_NEWS="news";
	public static final String MESSADE_EVENT="event";
	public static final String MESSADE_SUBSCRIBE="subscribe";
	public static final String MESSADE_UNSUBSCRIBE="unsubscribe";
	public static final String MESSADE_PARAM="key=e966d1a800e34066ad4cc287828f36fb";
	public static final String MESSAGE_API="http://www.tuling123.com/openapi/api";
	
	/**
	 * 将XML格式文件流转换为Map对象
	 * @param req
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static Map<String, String> xmlToMap(HttpServletRequest req) throws IOException, DocumentException{
		Map<String,String> map = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		reader.setEncoding("UTF-8");
		InputStream in = req.getInputStream();//获取的是一个xml文件流
		Document doc = reader.read(in);
		doc.setXMLEncoding("UTF-8");//设置根根元素的编码
		Element root = doc.getRootElement();
		List<Element> list = root.elements();
		for (Element element : list) {
			map.put(element.getName(), element.getStringValue());
		}
		return map;
	}
	/**
	 * 导入xstream包 里面提供将对象-转换XML的方法
	 * @param text
	 * @return
	 */
	public static String textMessageToXml(TextMessage text){
		XStream s = new XStream();
		s.alias("xml", text.getClass());//替换
		return s.toXML(text);
	}
	/**
	 * 将实体类转换成xml对象
	 * @param toUserName
	 * @param fromUserName
	 * @param content
	 * @return
	 */
	public static String intText(String toUserName,String fromUserName,String content){
		TextMessage tm = new TextMessage();
		tm.setContent(content);
		tm.setFromUserName(toUserName);
		tm.setToUserName(fromUserName);
		tm.setCreateTime(new Date().getTime()+"");
		tm.setMsgType(MessageUtil.MESSADE_TEXT);
		return textMessageToXml(tm);
	}

	/**
	 * 调用图灵API处理
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String tulinMsg(String content) throws UnsupportedEncodingException{
		//记得不要将整个链接 编码，只需要将中文编码即可
		String param = MessageUtil.MESSADE_PARAM+"&info="+URLEncoder.encode(content, "UTF-8");
		String result=ConnUtil.sendGet(MessageUtil.MESSAGE_API, param);
		System.out.println(result);		//用于调试消息输出
		//将字符串转换为json对象
		String str = JSONObject.fromObject(result).getString("text");
		return str;
	}
	/**
	 * Generate random imei and take md5
	 * @return
	 */
	public static String randomCharMd5(){
		String[] arr = new String[15];
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			arr[i]= (int)(Math.random()*10)+"";
			sb.append(arr[i]);
			
		}
		//System.out.println(sb.toString());
		StringBuffer str = new StringBuffer();
		try {
			MessageDigest instance =MessageDigest.getInstance("MD5");
			byte[] b=instance.digest(sb.toString().getBytes());
			for (byte c : b) {
				String hexString = Integer.toHexString(c & 0xff);
				if (hexString.length() < 2) {
                    hexString = "0"+hexString;
                }
				str.append(hexString);
			}
			System.out.println(str);
			return str.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String menuText(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append("我是能说会道.").append("\n");
		sb.append("		上知天文下知地理.").append("\n");
		sb.append("纵观中国历史长河五千年.").append("\n");
		sb.append("		不慎陨落凡间的智能小天使.").append("\n");
		sb.append("			--baby机器人").append("\n");
		sb.append("------------华丽分割线-----------").append("\n");
		sb.append("\n");
		sb.append("☆.二手汇APP任务格式").append("\n");
		sb.append("★.例如	AC-abcdefg:0000").append("\n");
		sb.append("☆.即对 推广码‘0000’下载一次").append("\n");
		sb.append("☆.新关注的送2次体验资格").append("\n");
		sb.append("★.time不足时 ↓↓↓").append("\n");
		sb.append("☆.请联系微信号:qq78732230").append("\n");
		sb.append("<a href='http://1212.ip138.com/ic.asp'>我的IP地址</a>");
		return sb.toString();
	}
}




