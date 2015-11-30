package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 第4个设置向导页
 * 
 * @author ning
 * 
 */
public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtecy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup4);

		cbProtecy = (CheckBox) findViewById(R.id.cb_protect);

		boolean protect = mPref.getBoolean("protect", false);

		// 根据sp保存的状态更新CheckBox
		if (protect) {
			cbProtecy.setText("防盗保护已开启");
			cbProtecy.setChecked(true);
		} else {
			cbProtecy.setText("防盗保护未开启");
			cbProtecy.setChecked(false);
		}

		// 当CheckBox发生变化时,回调此方法
		cbProtecy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbProtecy.setText("防盗保护已开启");
					mPref.edit().putBoolean("protect", true).commit();
				} else {
					cbProtecy.setText("防盗保护未开启");
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	/**
	 * 展示上一页
	 */
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// 两个界面之间的切换动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// 进入动画和退出动画
	}

	/**
	 * 展示下一页
	 */
	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		// 两个界面之间的切换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画

		mPref.edit().putBoolean("config", true).commit();// 表示已经展示过设置向导,下次进入手机防盗就不再展示
	}
}
