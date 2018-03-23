package com.weixin.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weixin.util.MessageUtil;

public class MySQLBasic extends HttpServlet {
	public static void main(String[] args) {
		String sql="UPDATE `wxdb` SET `experience`='Q' WHERE (`id`='3')";
		String str = MessageUtil.randomCharMd5().substring(0,6);
		sql = "INSERT INTO `wxdb` (`id`, `times`, `access_code`, `experience`, `code`, `wx_id`) VALUES (NULL, '2', '"+str+"', 'Y', ' "+str+" ',  ' "+str+" ');";
		sql="SELECT * FROM `wxdb` LIMIT 0, 1000";
		sql="UPDATE `wxdb` SET times=1, access_code='"+str+"' WHERE (code=3311)";
		MySQLBasic.executeAction(sql);
		//System.out.println(executeAction(sql));
		String regexAdd = "^ADD:[0-9]{4}----[0-9]{1,2}(----C)?$";
		if("ADD:3322----42".matches(regexAdd))System.out.println("1");
	}
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static synchronized Hashtable<String, String> executeAction(String sql){
		Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        Hashtable<String, String> tb = new Hashtable<String, String>(1000);
        try {
            /*****填写数据库相关信息(请查找数据库详情页)*****/
        	String databaseName = "gROQRIrgevqevQcbkFzA";
            String host = "sqld.duapp.com";
            String port = "4050";
            String username = "790bfda64cf544f0b59fd0396afa130f"; //用户AK
            String password = "23afd598482549de9d13add09fe13468"; //用户SK
            
        	/*
            String databaseName = "wxdb";
            String host = "127.0.0.1";
            String port = "3306";
            String username = "root"; //用户AK
            String password = ""; //用户SK
            */
            
            
            String driverName = "com.mysql.jdbc.Driver";
            String dbUrl = "jdbc:mysql://";
            String serverName = host + ":" + port + "/";
            String connName = dbUrl + serverName + databaseName;
            /******接着连接并选择数据库名为databaseName的服务器******/
            Class.forName(driverName);
            connection = DriverManager.getConnection(connName, username,
                    password);
            System.out.println(connName);
            stmt = connection.createStatement();
            /******至此连接已完全建立，就可对当前数据库进行相应的操作了*****/
            /******接下来就可以使用其它标准mysql函数操作进行数据库操作*****/
            //创建一个数据库表
/*            sql = "create table if not exists test_mysql(" +
                "id int primary key auto_increment," + "no int, " +
                "name varchar(1024)," + "key idx_no(no))";
            stmt.execute(sql);
            */
            if(sql.startsWith("UPDATE") || sql.startsWith("INSERT")){
            	stmt.execute(sql);
            }else{
            	rs = stmt.executeQuery(sql);
	            StringBuffer sb = null;
	            while(rs.next()){
	            	sb = new StringBuffer();
	            	sb.append(rs.getInt("times"));
	            	sb.append("----");
	            	sb.append(rs.getString("access_code"));
	            	sb.append("----");
	            	sb.append(rs.getString("experience"));
	            	sb.append("----");
	            	sb.append(rs.getString("wx_id"));
	            	tb.put(rs.getInt("code")+"", sb.toString());
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.err.println(e);
					e.printStackTrace();
				}
        }
		return tb;
    }
}
