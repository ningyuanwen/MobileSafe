package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * ��1��������ҳ
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
	 * ������û����һҳ,����д����
	 */
	@Override
	public void showPreviousPage() {
	}

	/**
	 * չʾ��һҳ
	 */
	@Override
	public void showNextPage() {

		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// ���붯�����˳�����
	}
}
