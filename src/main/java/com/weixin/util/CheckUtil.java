package com.weixin.util;

import java.security.MessageDigest;
import java.util.Arrays;
/**
 * 效验微信接入
 * @author xiao
 *	
 */
public class CheckUtil {
	private static final String token="weixin";
	/**
	 * 效验signature
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return 逻辑型
	 */
	public static boolean checksignature(String signature,String timestamp,String nonce){
		String[] ary = new String[]{token,timestamp,nonce};
		if(signature==null && timestamp==null && nonce==null){
			System.out.println("异常GET请求");
			return false;
		}
		Arrays.sort(ary);//字典排序
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<ary.length;i++){//组合成字符串
			sb.append(ary[i]);
		}
		//sha1验证
		String temp = getSha1(sb.toString());//sb是StringBuffer，先转换
		return temp.equals(signature);
	}
	/**
	 * sha1加密
	 * @param str
	 * @return
	 */
	private static String getSha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		MessageDigest mdTemp;
		try {
			mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes("UTF-8"));
			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b0 = md[i];
				buf[k++] = hexDigits[b0 >>> 4 & 0xf];
				buf[k++] = hexDigits[b0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}
}
