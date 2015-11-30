package com.nyw.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * MD5����
	 * 
	 * @param passowrd
	 * @return
	 */
	public static String encode(String password) {
		MessageDigest instance;
		try {
			instance = MessageDigest.getInstance("MD5");// ��ȡMD5�㷨����
			byte[] digst = instance.digest(password.getBytes());// ���ַ�������,�����ֽ�����

			StringBuffer sb = new StringBuffer();
			for (byte b : digst) {
				int i = b & 0xff;// ��ȡ�ֽڵĵͰ�λ��Чֵ
				String hexString = Integer.toHexString(i);// ������תΪ16����

				if (hexString.length() < 2) {
					hexString = "0" + hexString;// �����1λ�Ļ���0
				}
				sb.append(hexString);
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// û�и��㷨ʱ�׳��쳣�����ߵ�����
		}
		return "";
	}
}
