package com.nyw.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

/**
 * 服务状态的工具类
 * 
 * @author ning
 * 
 */
public class ServiceStatusUtils {

	/**
	 * 检测服务是否正在运行
	 * 
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx, String serviceName) {

		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> runningService = am.getRunningServices(100);// 系统所有正在运行的服务,参数是返回服务的个数
		for (RunningServiceInfo runningServiceInfo : runningService) {// 遍历系统所有正在运行的服务
			String className = runningServiceInfo.service.getClassName();// 获取服务名称
			// System.out.println(className);
			if (className.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}
