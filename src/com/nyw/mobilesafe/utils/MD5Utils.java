package com.nyw.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * MD5加密
	 * 
	 * @param passowrd
	 * @return
	 */
	public static String encode(String password) {
		MessageDigest instance;
		try {
			instance = MessageDigest.getInstance("MD5");// 获取MD5算法对象
			byte[] digst = instance.digest(password.getBytes());// 对字符串加密,返回字节数组

			StringBuffer sb = new StringBuffer();
			for (byte b : digst) {
				int i = b & 0xff;// 获取字节的低八位有效值
				String hexString = Integer.toHexString(i);// 将整数转为16进制

				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话补0
				}
				sb.append(hexString);
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// 没有该算法时抛出异常不会走到这里
		}
		return "";
	}
}
