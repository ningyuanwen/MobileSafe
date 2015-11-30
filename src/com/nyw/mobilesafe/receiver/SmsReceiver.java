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
 * 拦截短息
 * 
 * @author ning
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Object[] objects = (Object[]) intent.getExtras().get("pdus");

		for (Object object : objects) {// 短信最多是140个字节,超出的话会分多条短信发送,所以用数组,并且短信指令很短,所以for循环只执行一次
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			String originatingAddress = message.getOriginatingAddress();// 短信的来源手机号
			String messageBody = message.getMessageBody();// 短信的内容

			System.out.println(originatingAddress + ":" + messageBody);

			if ("#*alarm*#".equals(messageBody)) {
				// 播放报警音乐,即使手机静音也能播放音乐,因为使用的是媒体声音的通道,和铃声无关
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1f, 1f);
				player.setLooping(true);
				player.start();

				abortBroadcast();// 中断短信的传递,系统短信的app就无法收到内容
			} else if ("#*location*#".equals(messageBody)) {
				// 获取经纬度
				context.startService(new Intent(context, LocationService.class));// 开启定位服务

				SharedPreferences sp = context.getSharedPreferences("config",
						Context.MODE_PRIVATE);
				String location = sp.getString("location",
						"getting location...");

				System.out.println("location" + location);

				abortBroadcast();// 中断短信的传递,系统短信的app就无法收到内容
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// 远程锁屏
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				// 清除数据

			}
		}
	}

}
