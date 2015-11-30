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
 * �����ֻ����������Ĺ㲥
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);

		boolean protect = sp.getBoolean("protect", false);

		// ֻ���ڷ�������������ǰ����,�Ž���SIM���ж�
		if (protect) {
			String sim = sp.getString("sim", null);// ��ȡ��SIM�������к�

			if (!TextUtils.isEmpty(sim)) {
				// ��ȡ�������ֻ���SIM�������к�
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);

				String currentSim = tm.getSimSerialNumber() + "111";// ��ȡ��ǰ�ֻ���SIM�����к�

				// �ж�����SIM�������к��Ƿ����
				if (sim.equals(currentSim)) {
					System.out.println("SIM��δ����.");
				} else {
					System.out.println("SIM���Ѿ�������!");

					String phone = sp.getString("safe_phone", "");// ��ȡ��ȫ����

					// ���Ͷ��Ÿ���ȫ����
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null,
							"SIM card changed!", null, null);
				}
			}
		}
	}

}
