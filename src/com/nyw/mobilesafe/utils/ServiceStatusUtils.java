package com.nyw.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

/**
 * ����״̬�Ĺ�����
 * 
 * @author ning
 * 
 */
public class ServiceStatusUtils {

	/**
	 * �������Ƿ���������
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx, String serviceName) {

		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> runningService = am.getRunningServices(100);// ϵͳ�����������еķ���,�����Ƿ��ط���ĸ���
		for (RunningServiceInfo runningServiceInfo : runningService) {// ����ϵͳ�����������еķ���
			String className = runningServiceInfo.service.getClassName();// ��ȡ��������
			// System.out.println(className);
			if (className.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
