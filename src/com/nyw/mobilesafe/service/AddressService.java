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
 * �������ѵ�
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
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);// �������绰��״̬

		receiver = new OutCallReciver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);// ��̬ע��ȥ��㲥

	}

	class MyListener extends PhoneStateListener {

		// �����绰��״̬�����ı�ʱ
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// �绰����ʱ
				String address = AddressDao.getAddress(incomingNumber);// ������������ѯ������
				// Toast.makeText(AddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:// �绰����״̬
				if (mWm != null && view != null) {
					mWm.removeView(view);// ��window���Ƴ�����
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
	 * ����ȥ��Ĺ㲥������ ��ҪȨ��:android.permission.PROCESS_OUTGOING_CALLS
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

		unregisterReceiver(receiver);// ע��ȥ��㲥
	}

	/**
	 * �Զ�����������ظ���
	 */
	private void showToast(String text) {// �ο�ToastԴ��д��
		mWm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		// ��ȡ��Ļ���
		winWidth = mWm.getDefaultDisplay().getWidth();
		winHeight = mWm.getDefaultDisplay().getHeight();
		params = new WindowManager.LayoutParams();

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// ����ȡ����
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;

		params.type = WindowManager.LayoutParams.TYPE_PHONE;// ��ҪȨ��:android.permission.SYSTEM_ALERT_WINDOW
		params.gravity = Gravity.LEFT + Gravity.TOP;// ������λ�����������Ϸ�.Ҳ����(0,0)�����Ϸ���ʼ,������Ĭ�ϵ�����λ��

		params.setTitle("Toast");

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		// ���ø�����λ��,�������Ϸ���ƫ����
		params.x = lastX;
		params.y = lastY;

		// view = new TextView(this);
		view = View.inflate(this, R.layout.toast_address, null);

		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int style = mPref.getInt("address_style", 0);// ��ȡ�������ʾ����

		view.setBackgroundResource(bgs[style]);// ���ݴ洢����ʾ�������

		TextView tvText = (TextView) view.findViewById(R.id.tv_number);
		tvText.setText(text);

		mWm.addView(view, params);// ��view��ӵ���Ļ(Window)

		/*
		 * ����view��Ӧ�����¼�
		 */
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// ��ʼ���������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// �����ƶ�ƫ����
					int dx = endX - startX;
					int dy = endY - startY;

					// ���¸�����λ��
					params.x += dx;
					params.y += dy;

					// ��ֹ����ƫ����Ļ
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

					// ���³�ʼ���������
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_UP:
					// ��ָ�ɿ�,��¼�������
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
