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
 * �ֻ�����ҳ��333333
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
		boolean configed = mPrefs.getBoolean("config", false);// �ж��Ƿ�����������,Ĭ����û�н����

		if (configed) {
			// �����������
			setContentView(R.layout.activity_lost_find);

			// ����sp���°�ȫ����
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPrefs.getString("safe_phone", "");
			tvSafePhone.setText(phone);

			// ����sp��������ͼ��
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPrefs.getBoolean("protect", false);

			if (protect) {
				ivProtect.setImageResource(R.drawable.lock);
			} else {
				ivProtect.setImageResource(R.drawable.unlock);
			}

		} else {
			// û����������,��ת��������ҳ��
			startActivity(new Intent(this, Setup1Activity.class));
			finish();// �رյ�ǰҳ��
		}

	}

	/**
	 * ���½���������
	 */
	public void reEnter(View view) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();// �رյ�ǰҳ��

	}
}
