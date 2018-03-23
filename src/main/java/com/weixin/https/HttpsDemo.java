package com.weixin.https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
/**
 * Ignore the HTTPS certificate to send the request.
 * Support http or https of GET request
 * @author xiao
 *
 */
public class HttpsDemo {
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
                                            
    public static String httpsGet(String httpsUrl){
    	StringBuffer tempStr = new StringBuffer();
    	String responseContent="";
       HttpURLConnection conn = null;
       String state = null;
    	try {
                // Create a trust manager that does not validate certificate chains
            trustAllHosts();
    
    		URL url = new URL(httpsUrl);
    		
			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				conn = https;
			} else {
				conn = (HttpURLConnection)url.openConnection();
			}
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.connect();
			state=conn.getResponseCode() + " " + conn.getResponseMessage();
			System.out.println(state); 
			
            
			InputStream in = conn.getInputStream();
			conn.setReadTimeout(10*1000);
			BufferedReader rd = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String tempLine;
			while ((tempLine = rd.readLine()) != null) {
				tempStr.append(tempLine);
			}
			responseContent = tempStr.toString();
            System.out.println(responseContent);
			rd.close();
			in.close();
			conn.disconnect();
			return state+"\n"+responseContent;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
            e.printStackTrace();
        }
		return "status"+state;
    }
   
	    /**
	 * Trust every server - dont check for any certificate
	 */
    private static void trustAllHosts() {

	// Create a trust manager that does not validate certificate chains
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)  {
			
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
			
		}
	} };

	// Install the all-trusting trust manager
	try {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	} catch (Exception e) {
		e.printStackTrace();
	}
}
	public static void main(String[] args) {
		System.out.println(
		httpsGet("http://1212.ip138.com/ic.asp")
				);
	}

}
