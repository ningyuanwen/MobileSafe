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
 * 第2个设置向导页
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

		// 从sp中读取sim的值,判断是否勾选
		String sim = mPref.getString("sim", null);
		if (!TextUtils.isEmpty(sim)) {
			sivSim.setChecked(true);
		} else {

			sivSim.setChecked(false);
		}

		// SIM卡的CheckBox的监听事件
		sivSim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sivSim.isChecked()) {
					sivSim.setChecked(false);
					mPref.edit().remove("sim").commit();// 删除已经绑定的SIM卡

				} else {
					sivSim.setChecked(true);
					// 保存SIM卡信息
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String simSerialNumber = tm.getSimSerialNumber(); // 获取sim卡的序列号
					System.out.println("SIM卡的序列号:" + simSerialNumber);

					mPref.edit().putString("sim", simSerialNumber).commit();// 将SIM卡序列号保存到sp中
				}
			}
		});
	}

	/**
	 * 展示上一页
	 */
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();

		// 两个界面之间的切换动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// 进入动画和退出动画
	}

	/**
	 * 展示下一页
	 */
	public void showNextPage() {
		//判断SIM卡是否绑定,没有绑定就不能进入下一个页面
		String sim = mPref.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			ToastUtils.showToast(this, "未绑定本机SIM卡");
			return ;
		} 
		
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// 两个界面之间的切换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
	}

}
