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
 * ��4��������ҳ
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

		// ����sp�����״̬����CheckBox
		if (protect) {
			cbProtecy.setText("���������ѿ���");
			cbProtecy.setChecked(true);
		} else {
			cbProtecy.setText("��������δ����");
			cbProtecy.setChecked(false);
		}

		// ��CheckBox�����仯ʱ,�ص��˷���
		cbProtecy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbProtecy.setText("���������ѿ���");
					mPref.edit().putBoolean("protect", true).commit();
				} else {
					cbProtecy.setText("��������δ����");
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}

	/**
	 * չʾ��һҳ
	 */
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// ���붯�����˳�����
	}

	/**
	 * չʾ��һҳ
	 */
	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// ���붯�����˳�����

		mPref.edit().putBoolean("config", true).commit();// ��ʾ�Ѿ�չʾ��������,�´ν����ֻ������Ͳ���չʾ
	}
}
