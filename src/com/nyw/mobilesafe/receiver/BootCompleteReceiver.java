package com.nyw.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony.Sms;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

/*
 * 监听手机开机启动的广播
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);

		boolean protect = sp.getBoolean("protect", false);

		// 只有在防盗保护开启的前提下,才进行SIM卡判断
		if (protect) {
			String sim = sp.getString("sim", null);// 获取绑定SIM卡的序列号

			if (!TextUtils.isEmpty(sim)) {
				// 获取重启后手机的SIM卡的序列号
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);

				String currentSim = tm.getSimSerialNumber() + "111";// 获取当前手机的SIM卡序列号

				// 判断两个SIM卡的序列号是否相等
				if (sim.equals(currentSim)) {
					System.out.println("SIM卡未更换.");
				} else {
					System.out.println("SIM卡已经被更换!");

					String phone = sp.getString("safe_phone", "");// 读取安全号码

					// 发送短信给安全号码
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null,
							"SIM card changed!", null, null);
				}
			}
		}
	}

}
