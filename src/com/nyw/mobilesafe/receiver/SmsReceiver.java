package com.nyw.mobilesafe.receiver;

import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.service.LocationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.sax.StartElementListener;
import android.telephony.gsm.SmsMessage;

/**
 * ���ض�Ϣ
 * 
 * @author ning
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Object[] objects = (Object[]) intent.getExtras().get("pdus");

		for (Object object : objects) {// ���������140���ֽ�,�����Ļ���ֶ������ŷ���,����������,���Ҷ���ָ��ܶ�,����forѭ��ִֻ��һ��
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String originatingAddress = message.getOriginatingAddress();// ���ŵ���Դ�ֻ���
			String messageBody = message.getMessageBody();// ���ŵ�����

			System.out.println(originatingAddress + ":" + messageBody);

			if ("#*alarm*#".equals(messageBody)) {
				// ���ű�������,��ʹ�ֻ�����Ҳ�ܲ�������,��Ϊʹ�õ���ý��������ͨ��,�������޹�
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);
				player.setLooping(true);
				player.start();

				abortBroadcast();// �ж϶��ŵĴ���,ϵͳ���ŵ�app���޷��յ�����
			} else if ("#*location*#".equals(messageBody)) {
				// ��ȡ��γ��
				context.startService(new Intent(context, LocationService.class));// ������λ����

				SharedPreferences sp = context.getSharedPreferences("config",
						Context.MODE_PRIVATE);
				String location = sp.getString("location",
						"getting location...");

				System.out.println("location" + location);

				abortBroadcast();// �ж϶��ŵĴ���,ϵͳ���ŵ�app���޷��յ�����
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// Զ������
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				// �������

			}
		}
	}

}
