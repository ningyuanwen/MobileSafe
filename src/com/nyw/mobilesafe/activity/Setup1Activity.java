package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 第1个设置向导页
 * 
 * @author ning
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup1);
	}

	/*
	 * 本界面没有上一页,不用写功能
	 */
	@Override
	public void showPreviousPage() {
	}

	/**
	 * 展示下一页
	 */
	@Override
	public void showNextPage() {

		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// 两个界面之间的切换动画
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
	}
}
