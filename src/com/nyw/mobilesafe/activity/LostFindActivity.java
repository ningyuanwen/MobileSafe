package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机防盗页面333333
 * 
 * @author ning
 * 
 */
public class LostFindActivity extends Activity {
	private SharedPreferences mPrefs;

	private TextView tvSafePhone;
	private ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mPrefs = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPrefs.getBoolean("config", false);// 判断是否进入过设置向导,默认是没有进入过

		if (configed) {
			// 进入过设置向导
			setContentView(R.layout.activity_lost_find);

			// 根据sp更新安全号码
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPrefs.getString("safe_phone", "");
			tvSafePhone.setText(phone);

			// 根据sp更新锁的图标
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPrefs.getBoolean("protect", false);

			if (protect) {
				ivProtect.setImageResource(R.drawable.lock);
			} else {
				ivProtect.setImageResource(R.drawable.unlock);
			}

		} else {
			// 没进过设置向导,跳转到设置向导页面
			startActivity(new Intent(this, Setup1Activity.class));
			finish();// 关闭当前页面
		}

	}

	/**
	 * 重新进入设置向导
	 */
	public void reEnter(View view) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();// 关闭当前页面

	}
}
