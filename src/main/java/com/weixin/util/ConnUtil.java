package com.weixin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.weixin.https.HttpsDemo;


/**
 * 向指定URL发起一个GET请求,或者POST请求(字符输入流要用utf-8编码)
 * @param 是完整请求路径,带上http:// 或者https://
 * @param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
 * @return 请求后的响应文本
 */
public class ConnUtil{
	private static Logger logger = Logger.getLogger(ConnUtil.class);
	
	public static String sendGet(String url,String param){
		String result="";
		BufferedReader in = null;
		String urlName=url+"?"+param;
		//System.out.println(urlName);
		try {
			URL realUrl = new URL(urlName);//链接中不能有中文
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
         // 设置超时时间  
            conn.setConnectTimeout(10000);  
            conn.setReadTimeout(10000);  
            // 建立实际的连接  
            conn.connect();  
            // 获取所有响应头字段  
//            Map< String,List< String>> map = conn.getHeaderFields();  
            // 遍历所有的响应头字段  
            /*for (String key : map.keySet()) {  
                System.out.println(key + "--->" + map.get(key));  
            } */ 
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			result = "Get:服务器连接失败！";
			e.printStackTrace();
		}// 使用finally块来关闭输入流  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }
		
		return result;
	}

	public static String sendPost(String url,String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {  
            URL realUrl = new URL(url);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // 设置超时时间  
            conn.setConnectTimeout(10000);  
            conn.setReadTimeout(10000);  
            // 发送POST请求必须设置如下两行  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            // 获取URLConnection对象对应的输出流  
            out = new PrintWriter(conn.getOutputStream());  
            // 发送请求参数  
            out.print(param);  
            // flush输出流的缓冲  
            out.flush();  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  
            String line;  
            while ((line = in.readLine()) != null) {  
                result += "\n" + line;  
            }  
        } catch (Exception e) {  
            result = "Post:服务器连接失败！";  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输出流、输入流  
        finally {  
            try {  
                if (out != null) {
                    out.close();  
                }  
                if (in != null) {
                    in.close();  
                }
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;
	}
	
	
	public static void setProperties() throws IOException{
		OutputStream fos = null;
		Properties p = new Properties();
		try {
			String b=ConnUtil.class.getResource("/home/admin/runtime/test.properties").getPath();
			System.out.println(b);
			if  (new File("/home/admin/runtime/test.properties") .exists())     
			{ 
				logger.info("cun zai");
			}
			fos=new FileOutputStream(b);
			p.setProperty("sssss", "你好");
			p.store(fos, "");
			InputStream in=ConnUtil.class.getResourceAsStream("/home/admin/runtime/test.properties");
			p.load(in);
			logger.info(p);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos !=null){
				fos.close();
			}
		}
	}
	public static boolean httpsAccess(String value){
		String strRandom = MessageUtil.randomCharMd5();
		String url ="https://www.ershouhui.com/api/AppSpread/AddAppSpread?cid=" + strRandom + "&terminalType=TT03&invitationCode="+value; 
		String data = HttpsDemo.httpsGet(url);
		if(data.indexOf("200 OK")!=-1 && data.indexOf("true")!=-1){
			return true;
		}
		return false;
		
	}
	public static String getClassPath(){
		String path= new Object(){
			public String getPath(){
				return this.getClass().getResource("test.properties").getPath();
			}
		}.getPath();
		return path;
	}
	public static void main(String[] args) throws IOException {
		logger.error("dssfdsfdsff");
		logger.debug("Test");
		logger.info("fdsfdfdsfdsdsgsg");
		logger.warn("ddddddddddddddddddddddd");
		logger.info(System.getProperty("user.dir"));
		//httpsAccess("9998");
		String content="AC-abcd12:1234";
		String reg = "^AC-\\w{6}:[0-9]{4}$";
		if(content.matches(reg)){
			int c =content.indexOf(":");
			content = content.substring(c+1);
			System.out.println(content);
		}
	}
}
