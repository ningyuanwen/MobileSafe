package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ��2��������ҳ
 * 
 * @author ning
 * 
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView sivSim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup2);

		sivSim = (SettingItemView) findViewById(R.id.siv_sim);

		// ��sp�ж�ȡsim��ֵ,�ж��Ƿ�ѡ
		String sim = mPref.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			sivSim.setChecked(true);
		} else {

			sivSim.setChecked(false);
		}

		// SIM����CheckBox�ļ����¼�
		sivSim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sivSim.isChecked()) {
					sivSim.setChecked(false);
					mPref.edit().remove("sim").commit();// ɾ���Ѿ��󶨵�SIM��

				} else {
					sivSim.setChecked(true);
					// ����SIM����Ϣ
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber(); // ��ȡsim�������к�
					System.out.println("SIM�������к�:" + simSerialNumber);

					mPref.edit().putString("sim", simSerialNumber).commit();// ��SIM�����кű��浽sp��
				}
			}
		});
	}

	/**
	 * չʾ��һҳ
	 */
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// ���붯�����˳�����
	}

	/**
	 * չʾ��һҳ
	 */
	public void showNextPage() {
		//�ж�SIM���Ƿ��,û�а󶨾Ͳ��ܽ�����һ��ҳ��
		String sim = mPref.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			ToastUtils.showToast(this, "δ�󶨱���SIM��");
			return ;
		} 
		
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// ���붯�����˳�����
	}

}
