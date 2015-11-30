package com.nyw.mobilesafe.service;

import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.activity.ToastUtils;
import com.nyw.mobilesafe.db.dao.AddressDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.GpsStatus.Listener;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 来电提醒的
 * 
 * @author ning
 * 
 */
public class AddressService extends Service {

	private TelephonyManager tm;
	private MyListener listener;
	private OutCallReciver receiver;
	private WindowManager mWm;
	private View view;
	private SharedPreferences mPref;
	private int startX;
	private int startY;
	private int winWidth;
	private int winHeight;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// 监听来电话的状态

		receiver = new OutCallReciver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);// 动态注册去电广播

	}

	class MyListener extends PhoneStateListener {

		// 当来电话的状态发生改变时
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话铃响时
				String address = AddressDao.getAddress(incomingNumber);// 根据来电号码查询归属地
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:// 电话闲置状态
				if (mWm != null && view != null) {
					mWm.removeView(view);// 从window中移除界面
					view = null;
				}
				break;
			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}
	}

	/**
	 * 监听去电的广播接收者 需要权限:android.permission.PROCESS_OUTGOING_CALLS
	 * 
	 * @author ning
	 * 
	 */
	class OutCallReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = AddressDao.getAddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showToast(address);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);

		unregisterReceiver(receiver);// 注销去电广播
	}

	/**
	 * 自定义来电归属地浮窗
	 */
	private void showToast(String text) {// 参考Toast源码写的
		mWm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		// 获取屏幕宽高
		winWidth = mWm.getDefaultDisplay().getWidth();
		winHeight = mWm.getDefaultDisplay().getHeight();
		params = new WindowManager.LayoutParams();

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不获取焦点
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;

		params.type = WindowManager.LayoutParams.TYPE_PHONE;// 需要权限:android.permission.SYSTEM_ALERT_WINDOW
		params.gravity = Gravity.LEFT + Gravity.TOP;// 将重心位置设置在左上方.也就是(0,0)从左上方开始,而不是默认的中心位置

		params.setTitle("Toast");

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		// 设置浮窗的位置,基于左上方的偏移量
		params.x = lastX;
		params.y = lastY;

		// view = new TextView(this);
		view = View.inflate(this, R.layout.toast_address, null);

		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int style = mPref.getInt("address_style", 0);// 读取保存的提示框风格

		view.setBackgroundResource(bgs[style]);// 根据存储的提示框风格更新

		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);

		mWm.addView(view, params);// 将view添加到屏幕(Window)

		/*
		 * 设置view响应触摸事件
		 */
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新浮窗的位置
					params.x += dx;
					params.y += dy;

					// 防止坐标偏离屏幕
					if (params.x < 0) {
						params.x = 0;
					} else if (params.y < 0) {
						params.y = 0;
					} else if (params.x > winWidth - view.getWidth()) {
						params.x = winWidth - view.getWidth();
					} else if (params.y > winHeight - view.getHeight()) {
						params.y = winHeight - view.getHeight();
					}
					mWm.updateViewLayout(view, params);

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					// 手指松开,记录下坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", params.x);
					edit.putInt("lastY", params.y);
					edit.commit();
					break;

				default:
					break;
				}
				return true;
			}
		});
	}
}
